#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Compact Productive Bees feeder and honey comparison gallery."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "productivebees_gallery"
ENVELOPE = (159, 99, 159, 173, 103, 169)
SETUP_COMMANDS = (
    "fill 159 99 164 173 99 166 minecraft:stone",
    "fill 159 100 164 173 100 164 minecraft:glass",
    "fill 159 100 166 173 100 166 minecraft:glass",
    "fill 159 100 165 159 101 165 minecraft:glass",
    "fill 173 100 165 173 101 165 minecraft:glass",
)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str
    nbt: str | None = None


PLACEMENTS = (
    Placement(
        "oak-slab-control",
        "oak slab stock control",
        160,
        100,
        161,
        "minecraft:oak_slab[type=bottom,waterlogged=false]",
        "stock oak bottom slab",
    ),
    Placement(
        "feeder-default-bottom",
        "default smooth-stone feeding slab",
        163,
        100,
        161,
        "productivebees:feeder[facing=south,type=bottom,waterlogged=false,honeylogged=false]",
        "smooth-stone bottom slab",
    ),
    Placement(
        "feeder-oak-bottom",
        "oak feeding slab bottom",
        166,
        100,
        161,
        "productivebees:feeder[facing=south,type=bottom,waterlogged=false,honeylogged=false]",
        "oak bottom slab",
        '{baseBlock:"minecraft:oak_slab"}',
    ),
    Placement(
        "feeder-oak-top",
        "oak feeding slab top",
        169,
        100,
        161,
        "productivebees:feeder[facing=south,type=top,waterlogged=false,honeylogged=false]",
        "oak top slab",
        '{baseBlock:"minecraft:oak_slab"}',
    ),
    Placement(
        "feeder-oak-double",
        "oak double feeding slab",
        172,
        100,
        161,
        "productivebees:feeder[facing=south,type=double,waterlogged=false,honeylogged=false]",
        "oak full block",
        '{baseBlock:"minecraft:oak_slab"}',
    ),
    Placement(
        "honey-source",
        "honey source feeding a bounded trough",
        160,
        100,
        165,
        "productivebees:honey[level=0]",
        "golden source and naturally descending flow",
    ),
)
