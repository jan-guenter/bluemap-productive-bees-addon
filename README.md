# BlueMap Productive Bees Add-on

A Java 21 BlueMap add-on for the exact `productivebees-1.21.1-13.13.5` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: staging prototype. It restores the Feeding Slab's client-rendered slab
body, including its saved material and bottom/top/double form, and routes the
Productive Bees honey block through BlueMap's native liquid renderer with the
exact installed textures and fixed client tint.

## Build

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the bounded review
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock
behavior; the add-on creates no custom world state.

Set `-Dbluemap.productivebees.disabled=true` to leave the exact profile inactive.

## Scope boundary

The staging scope is limited to the observed blank feeder body and absent honey
fluid geometry. Feeder inventory items, bottled items, centrifuge contents,
jarred bees, amber subjects, particles, and animation remain stock until a
separate observed comparison justifies expanding scope.

No Productive Bees binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
