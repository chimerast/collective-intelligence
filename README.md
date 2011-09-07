## 2.3.1 ユークリッド距離によるスコア
### sqrt(pow(5 - 4, 2) + pow(4 - 1, 2))
    3.1622776601683795
### 1 / (1 + sqrt(pow(5 - 4, 2) + pow(4 - 1, 2)))
    0.2402530733520421
### Lisa RoseとGene Seymourのユークリッド距離
    0.14814814814814814

## 2.3.2 ピアソン相関によるスコア
### Lisa RoseとGene Seymourのピアソン相関
    0.39605901719066977

## 2.3.4 評者をランキングする
### Tobyに似た評者を探す
    (0.9912407071619299,Lisa Rose)
    (0.9244734516419049,Mick LaSalle)
    (0.8934051474415647,Claudia Puig)

## 2.4 アイテムを推薦する
### ピアソン相関でToby用の商品を推薦
    (3.3477895267131013,The Night Listener)
    (2.832549918264162,Lady in the Water)
    (2.530980703765565,Just My Luck)
### ユークリッド距離でToby用の商品を推薦
    (3.5002478401415877,The Night Listener)
    (2.7561242939959363,Lady in the Water)
    (2.461988486074374,Just My Luck)

## 2.5 似ている商品
### Superman Returnsに似ている商品を探す
    (0.6579516949597695,You, Me and Dupree)
    (0.4879500364742689,Lady in the Water)
    (0.11180339887498941,Snakes on a Plane)
    (-0.1798471947990544,The Night Listener)
    (-0.42289003161103106,Just My Luck)
### Just My Luckを見ていない評者の中で高い評価をつけそうな人を予測する
    (4.0,Michael Phillips)
    (3.0,Jack Matthews)

## 2.6.1 del.icio.usのAPI
### programmingに関する人気のブックマーク
    Map(u -> http://www.codecademy.com/#!/exercise/0, d -> Learn to code | Codecademy, t -> code|programming|web2.0|webdesign|software|tools, dt -> 2011-08-18T22:04:13Z)
    Map(u -> http://scottchacon.com/2011/08/31/github-flow.html, d -> Scott Chacon on the Interwebs, t -> git|github|programming|workflow|development|productivity, dt -> 2011-08-31T16:05:03Z)
    Map(u -> http://www.codecademy.com/, d -> http://www.codecademy.com/, t -> web|programming|tutorial, dt -> 2011-08-18T21:43:39Z)
    Map(u -> http://yannesposito.com/Scratch/en/blog/Learn-Vim-Progressively/, d -> Learn Vim Progressively, t -> vim|programming, dt -> 2011-08-29T10:54:58Z)
    Map(u -> http://lifehacker.com/5836220/codecademy-is-a-free-interactive-webapp-that-teaches-you-how-to-code, d -> Codecademy Is a Free, Interactive Webapp That Teaches You How to Code, t -> computer|programming, dt -> 2011-08-31T20:13:19Z)

## 2.6.2 データセットを作る
### del.icio.usからprogrammingタグの人気のURLをブックマークしたユーザを抜いてくる
    List(oherrmann, wirthfalls, boogie, fjsj, nxecuda, mcb0209, mikechoi2k, creativism, bd808, raulhmacias, javadi82, colarte, Ode0n, ilegiona, whichlight, pocketcowboy, minch, tebeka, toscanolayman, razzmataz, bloodless__, osopulgoso, bmacauley, grischaandreew, rafael_hrdz, teki321, segalahrens, Inetgate, amonroy, esopop, wedgedkc, drsnyder, garethjenkins, rshea, acohen5212, mike.otoole, jeramyRR, tcol, buki79, keogh, sikeshitt, _af83, desperado.jones, scottnelsonsmith, k_mccarthy, gnrfan, paul_mckee, euler, jluddr, BarrieDirk, rampion, bhobbit, jeffrey.schoolcraft, froling, rehzus, somehowfind, James.seo, adharmad, yvmarques, Ericssonlabs, ameqamc, twashing, enlil, mcvaychapin, jelleferinga, tompudliner, roland_mai, miaridge, lfborjas, strozw, mallamanis, mvaline, captshadg, bombox, elg0nz, ppitre13, hyneslerma, reichertballew, domingogallardo, jchastang2001, u_al_mihai, gnarl, ypwong, ppanyukov, moeen49, cantyestrellaas, MarkEhrhardt, pfeifervanwinkle, metapsj)
    jeffrey.schoolcraft: Map(http://designm.ag/inspiration/20-examples-of-graphic-icons-in-web-design/ -> 0.0, http://groups.google.com/group/comp.lang.forth/browse_thread/thread/b80efd8a8d4dd30c# -> 0.0, http://brainz.org/8-back-school-kid-types/ -> 0.0, http://weblog.rubyonrails.org/2011/8/31/rails-3-1-0-has-been-released?utm_source=rubyweekly&utm_medium=email -> 0.0, http://www.documentcloud.org/home -> 0.0, ...)
    ilegiona: Map(http://designm.ag/inspiration/20-examples-of-graphic-icons-in-web-design/ -> 0.0, http://groups.google.com/group/comp.lang.forth/browse_thread/thread/b80efd8a8d4dd30c# -> 0.0, http://brainz.org/8-back-school-kid-types/ -> 0.0, http://weblog.rubyonrails.org/2011/8/31/rails-3-1-0-has-been-released?utm_source=rubyweekly&utm_medium=email -> 0.0, http://www.documentcloud.org/home -> 0.0, ...)
    enlil: Map(http://designm.ag/inspiration/20-examples-of-graphic-icons-in-web-design/ -> 0.0, http://groups.google.com/group/comp.lang.forth/browse_thread/thread/b80efd8a8d4dd30c# -> 0.0, http://brainz.org/8-back-school-kid-types/ -> 0.0, http://weblog.rubyonrails.org/2011/8/31/rails-3-1-0-has-been-released?utm_source=rubyweekly&utm_medium=email -> 0.0, http://www.documentcloud.org/home -> 0.0, ...)
    roland_mai: Map(http://designm.ag/inspiration/20-examples-of-graphic-icons-in-web-design/ -> 0.0, http://groups.google.com/group/comp.lang.forth/browse_thread/thread/b80efd8a8d4dd30c# -> 0.0, http://brainz.org/8-back-school-kid-types/ -> 0.0, http://weblog.rubyonrails.org/2011/8/31/rails-3-1-0-has-been-released?utm_source=rubyweekly&utm_medium=email -> 0.0, http://www.documentcloud.org/home -> 0.0, ...)
    sikeshitt: Map(http://designm.ag/inspiration/20-examples-of-graphic-icons-in-web-design/ -> 0.0, http://groups.google.com/group/comp.lang.forth/browse_thread/thread/b80efd8a8d4dd30c# -> 0.0, http://brainz.org/8-back-school-kid-types/ -> 0.0, http://weblog.rubyonrails.org/2011/8/31/rails-3-1-0-has-been-released?utm_source=rubyweekly&utm_medium=email -> 0.0, http://www.documentcloud.org/home -> 0.0, ...)

## 2.6.3 ご近所さんとリンクの推薦
### ユーザーに似た嗜好のユーザを探す
    ユーザ名: jluddr
    List((0.08757478140819144,rehzus), (0.05378125479368002,javadi82), (0.05378125479368002,grischaandreew), (0.05378125479368002,u_al_mihai), (0.05378125479368002,bombox))
### ユーザが好みそうなリンクを探す
    List((0.27910915249298895,http://yannesposito.com/Scratch/en/blog/Learn-Vim-Progressively/), (0.2553811414211311,http://www.catonmat.net/blog/browserling-open-sources-90-node-modules/), (0.23728011071857802,http://www.rehabinfo.net/blog/drug-addiction-signs/), (0.2135520996467202,http://blog.modis.com/job-seekers/evolution-of-labor/), (0.19153585606584833,http://imakewebthings.github.com/deck.js/))
### 特定のリンクに似たリンクを探す
    URL: http://yannesposito.com/Scratch/en/blog/Learn-Vim-Progressively/
    List((0.40374423139992327,http://chriseidhof.tumblr.com/post/9539831015/on-minimalism), (0.30818713486385046,http://ldapjs.org/), (0.30818713486385046,http://twitter.github.com/bootstrap/), (0.2838635453817454,http://www.perldancer.org/), (0.2838635453817454,https://nealpoole.com/blog/2011/08/cross-site-scripting-via-error-reporting-notices-in-php/))

## 2.7.1 アイテム間の類似度のデータセットを作る
    (Superman Returns,List((0.16666666666666666,Snakes on a Plane), (0.10256410256410256,The Night Listener), (0.09090909090909091,Lady in the Water), (0.06451612903225806,Just My Luck), (0.05333333333333334,You, Me and Dupree)))
    (The Night Listener,List((0.2857142857142857,Lady in the Water), (0.18181818181818182,Snakes on a Plane), (0.15384615384615385,Just My Luck), (0.14814814814814814,You, Me and Dupree), (0.10256410256410256,Superman Returns)))
    (Just My Luck,List((0.2222222222222222,Lady in the Water), (0.18181818181818182,You, Me and Dupree), (0.15384615384615385,The Night Listener), (0.10526315789473684,Snakes on a Plane), (0.06451612903225806,Superman Returns)))
    (You, Me and Dupree,List((0.4,Lady in the Water), (0.18181818181818182,Just My Luck), (0.14814814814814814,The Night Listener), (0.05333333333333334,Superman Returns), (0.05128205128205128,Snakes on a Plane)))
    (Snakes on a Plane,List((0.2222222222222222,Lady in the Water), (0.18181818181818182,The Night Listener), (0.16666666666666666,Superman Returns), (0.10526315789473684,Just My Luck), (0.05128205128205128,You, Me and Dupree)))
    (Lady in the Water,List((0.4,You, Me and Dupree), (0.2857142857142857,The Night Listener), (0.2222222222222222,Snakes on a Plane), (0.2222222222222222,Just My Luck), (0.09090909090909091,Superman Returns)))

## 2.7.2 推薦を行う
### アイテムベースの表からToby向け推薦を行う
    (3.1826347305389224,The Night Listener)
    (2.5983318700614575,Just My Luck)
    (2.4730878186968837,Lady in the Water)

## 2.8 MovieLensのデータセットを使う
### 87番のユーザの評価を出力
    274: Map(Up Close and Personal (1996) -> 1.0, Dragonheart (1996) -> 3.0, Mr. Holland's Opus (1995) -> 5.0, Ghost and the Darkness, The (1996) -> 2.0, White Squall (1996) -> 3.0, ...)
    618: Map(Adventures of Priscilla, Queen of the Desert, The (1994) -> 2.0, Rob Roy (1995) -> 2.0, Clear and Present Danger (1994) -> 3.0, Dazed and Confused (1993) -> 4.0, Mr. Holland's Opus (1995) -> 3.0, ...)
    323: Map(Sling Blade (1996) -> 4.0, Breakdown (1997) -> 3.0, Vertigo (1958) -> 4.0, Taxi Driver (1976) -> 5.0, Mr. Holland's Opus (1995) -> 3.0, ...)
    437: Map(Cat People (1982) -> 3.0, Quiz Show (1994) -> 4.0, Mighty Aphrodite (1995) -> 4.0, Mr. Holland's Opus (1995) -> 4.0, Ed Wood (1994) -> 4.0, ...)
    432: Map(Breakdown (1997) -> 3.0, Freeway (1996) -> 4.0, Con Air (1997) -> 3.0, Mr. Holland's Opus (1995) -> 4.0, Daylight (1996) -> 3.0, ...)
### 87番のユーザベースの推薦
    (5.0,Santa with Muscles (1996))
    (5.0,They Made Me a Criminal (1939))
    (5.0,Star Kid (1997))
    (5.0,Saint of Fort Washington, The (1993))
    (5.0,Marlene Dietrich: Shadow and Light (1996) )
### アイテムベースの推薦
    (5.0,My Fair Lady (1964))
    (5.0,Rock, The (1996))
    (5.0,Rear Window (1954))
    (5.0,Stand by Me (1986))
    (5.0,Nell (1994))

## 2.10.1 Tanimoto係数
### Tanimoto係数でToby用の商品を推薦
    (3.428242075208925,The Night Listener)
    (2.7945514303777657,Lady in the Water)
    (2.3921287261885347,Just My Luck)

## 2.10.2 タグの類似性
### del.icio.usからprogrammingタグの人気のURLについているタグを抜いてくる
    List(plugin, howto, reference, lists, business, to_do, design, amazon, via:zite, plugins, guide, map, ec2, programming_books, millionsongdataset, mapreduce, programming, cs, book, to_read, codecomplete, hacking, music, via:brmichel, via:packrati.us, developer, programacion, tools, introductiontoalgorithms, top10, エディタ, mrjob, tutorial, tldr, プラグイン, analysis, hadoop, education, reduce, transcoding, fun, books, books_toread, books_tobuy, python, editors, development, analytics, elastic-computing, MSD, toread, recommendations, mobile, self-improvement, top, structureandinterpretationofcomputerprograms, computer-science, softwareengineering, web-development, list, vim, grid-computing)
    analytics: Map(http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.telestream.net/ -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, http://www.tripwiremagazine.com/2010/07/55-great-web-development-frameworks.html -> 0.0, http://www.encoding.com/ -> 0.0, ...)
    music: Map(http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.telestream.net/ -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, http://www.tripwiremagazine.com/2010/07/55-great-web-development-frameworks.html -> 0.0, http://www.encoding.com/ -> 0.0, ...)
    education: Map(http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.telestream.net/ -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, http://www.tripwiremagazine.com/2010/07/55-great-web-development-frameworks.html -> 0.0, http://www.encoding.com/ -> 0.0, ...)
    tldr: Map(http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.telestream.net/ -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, http://www.tripwiremagazine.com/2010/07/55-great-web-development-frameworks.html -> 0.0, http://www.encoding.com/ -> 0.0, ...)
    to_do: Map(http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.telestream.net/ -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, http://www.tripwiremagazine.com/2010/07/55-great-web-development-frameworks.html -> 0.0, http://www.encoding.com/ -> 0.0, ...)
### howtoに似たタグを探す
    List((0.27062117755084586,tutorial), (0.20571294117423353,programming), (0.19790117674024135,reference), (0.17658405698683324,development), (0.10098599655969254,toread))
### programmingタグのついていない似たリンクを探す
    (0.3380927718026311,http://www.jackkinsella.ie/2011/09/05/textmate-to-vim.html)
    (0.31228080044711365,https://github.com/bryanbibat/rails-3_0-tutorial)
    (0.2541030885586514,http://www.khanacademy.org/)
    (0.21869787740651037,http://yannesposito.com/Scratch/en/blog/Learn-Vim-Progressively/)
    (0.19249621292310004,http://skytechgeek.com/2011/09/6-useful-applications-for-mobile-development/)

