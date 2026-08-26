#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated Productive Bees gallery without starting Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        raise ValueError("load tag differs from the exact namespace")
    if len(cases.PLACEMENTS) != 6:
        raise ValueError("review gallery must contain exactly six anchor cases")
    if len({placement.case_id for placement in cases.PLACEMENTS}) != 6:
        raise ValueError("case IDs must be unique")
    if sum("productivebees:feeder" in row.block_state for row in cases.PLACEMENTS) != 4:
        raise ValueError("review gallery must contain four feeder forms")
    if sum("productivebees:honey" in row.block_state for row in cases.PLACEMENTS) != 1:
        raise ValueError("review gallery must contain one honey source")
    if cases.PLACEMENTS[0].block_state != (
        "minecraft:oak_slab[type=bottom,waterlogged=false]"
    ):
        raise ValueError("first case must remain the stock oak-slab control")
    minimum_x, minimum_y, minimum_z, maximum_x, maximum_y, maximum_z = (
        cases.ENVELOPE
    )
    for placement in cases.PLACEMENTS:
        if not (
            minimum_x <= placement.x <= maximum_x
            and minimum_y <= placement.y <= maximum_y
            and minimum_z <= placement.z <= maximum_z
        ):
            raise ValueError(f"placement escaped its bounded envelope: {placement.case_id}")
        if placement.nbt is not None and ("\n" in placement.nbt or "\r" in placement.nbt):
            raise ValueError(f"NBT must stay on one command line: {placement.case_id}")

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    if len(re.findall(r"^setblock ", functions, re.MULTILINE)) != len(
        cases.PLACEMENTS
    ):
        raise ValueError("setblock count differs from the case table")
    expected_merges = sum(row.nbt is not None for row in cases.PLACEMENTS)
    if len(re.findall(r"^data merge block ", functions, re.MULTILINE)) != expected_merges:
        raise ValueError("data-merge count differs from the case table")
    lowered = functions.lower()
    for forbidden in ("summon ", "op ", "deop ", "stop "):
        if forbidden in lowered:
            raise ValueError(f"forbidden review command: {forbidden}")
    print("review gallery lint passed: six bounded anchors")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
