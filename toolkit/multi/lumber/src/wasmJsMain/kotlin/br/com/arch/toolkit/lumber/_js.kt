package br.com.arch.toolkit.lumber

@JsFun("() => (new Error()).stack ?? ''")
external fun jsStack(): String

@JsFun("(lvl, msg) => { const c = console; (c[lvl] ?? c.log).call(c, msg); }")
external fun jsConsole(lvl: String, msg: String)
