![F3 API](https://github.com/Roundaround/mc-fabric-f3-api/raw/main/assets/title-round.png)

![](https://img.shields.io/badge/Loader-Fabric-313e51?style=for-the-badge)
![](https://img.shields.io/badge/MC-1.21.6-313e51?style=for-the-badge)
![](https://img.shields.io/badge/Side-Client-313e51?style=for-the-badge)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/f3-api?style=flat&logo=modrinth&color=00AF5C)](https://modrinth.com/mod/f3-api)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1?style=flat&logo=curseforge&color=F16436)](https://www.curseforge.com/minecraft/mc-mods/f3-api)
[![GitHub Repo stars](https://img.shields.io/github/stars/Roundaround/mc-fabric-f3-api?style=flat&logo=github)](https://github.com/Roundaround/mc-fabric-f3-api)

[![Support me on Ko-fi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/kofi-singular-alt_vector.svg)](https://ko-fi.com/roundaround)

---

API & UI enabling easy compatibility among vanilla and mods with F3 key binds.

For end users, the mod provides a familiar key binds screen specifically for all the F3 + ? debug key binds in the vanilla game. For a few select mods out there (see below), it also automatically registers the appropritate key binds to ensure compatibility.

![Edit key binds screen](https://github.com/Roundaround/mc-fabric-f3-api/raw/main/assets/1.21.6-edit.png)

For mod developers, it exposes an "api" package that you can build your mod on top of to easily
register your debug key binds without having to write all the custom mixins yourself! See the API
section for more info!

---

## Debug key binds screen

TODO

- Get to it from:
  - Options -> Controls -> Debug Key Binds...
  - ModMenu -> F3 API options
- Allow rebinding any debug (F3) key bind in the same way as normal key binds, but with the addition
  of modifiers (control, shift, & alt).

## Mod developer API

TODO
