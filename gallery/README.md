# Review gallery

This bounded gallery compares the Productive Bees Feeding Slab against a stock
oak slab and exercises its default, selected-material, top, and double forms.
A contained honey trough supplies one source and the naturally settled flowing
levels needed to compare BlueMap's fluid surface with the client.

Regenerate and validate it with:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/productivebees-gallery.zip
```

The gallery contains no Productive Bees assets or captured meshes; all visuals
come from the exact operator-installed mod artifact.
