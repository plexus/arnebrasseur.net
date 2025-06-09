---
title: Piglet Roguelike Demo
---

This is a work-in-progress of a Roguelike game written in
[Piglet](https://github.com/piglet-lang/piglet), using
[Rot.js](https://ondras.github.io/rot.js/hp/). You can find the source here:
[github:plexus/pigrot](https://github.com/plexus/pigrot).

Controls: arrow keys, escape, enter (inside menu), d: drop (inside inventory)

```inline-html
<script type="importmap">
{"imports":{
"astring":"https://cdn.jsdelivr.net/npm/astring@1.9.0/dist/astring.mjs",
"rot-js":"https://cdn.jsdelivr.net/npm/rot-js@2.2.1/lib/index.js"
}}
</script>
<script type="module" src="https://cdn.jsdelivr.net/npm/piglet-lang@0.1.42/lib/piglet/browser/main.mjs"></script>
<script type="piglet">
(await (load-package "https://cdn.jsdelivr.net/npm/piglet-roguelike-demo@0.2.0"))
(await (import 'https://arnebrasseur.net/pigrot:main))
</script>
<style>
#app {
width: 90vw;
height: 90vh;
}
body { max-width: 95%; }
canvas {
max-width: 80%;
max-height: 80%;
aspect-ratio: 60/20;
border: 5px solid #7d7d8e;
padding: 5px;
}
</style>
<div id="app"></div>
```

