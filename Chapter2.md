## 2.3.1 ユークリッド距離によるスコア
### sqrt(pow(4.5 - 4, 2) + pow(1 - 2, 2))
    1.118033988749895
### 1 / (1 + sqrt(pow(4.5 - 4, 2) + pow(1 - 2, 2)))
    0.4721359549995794
### Lisa RoseとGene Seymourのユークリッド距離
    0.29429805508554946

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
    (3.4571286944914226,The Night Listener)
    (2.778584003814924,Lady in the Water)
    (2.422482042361917,Just My Luck)

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
    Map(u -> http://musicmachinery.com/2011/09/04/how-to-process-a-million-songs-in-20-minutes/, d -> How to process a million songs in 20 minutes « Music Machinery, t -> programming|mapreduce|distributed_computing, dt -> 2011-09-04T16:52:22Z)
    Map(u -> http://www.internetsecuritydb.com/2011/09/top-ten-most-influential-programming.html, d -> Internet Security: Top Ten Most Influential Programming Books of All Time, t -> programming, dt -> 2011-09-04T18:03:40Z)
    Map(u -> http://stevelosh.com/blog/2011/09/writing-vim-plugins/, d -> Writing Vim Plugins / Steve Losh, t -> vim|plugins|development|programming|plugin|howto|tutorial|dev, dt -> 2011-09-06T13:47:19Z)
    Map(u -> http://www.cix.co.uk/~smallmemory/book.html, d -> Small Memory Software, t -> book|data|memory|software|embedded|architecture|programming|2read, dt -> 2008-03-12T11:18:55Z)
    Map(u -> http://www.codecademy.com/#!/exercise/0, d -> Learn to code | Codecademy, t -> javascript|learning|tutorial|programming|code, dt -> 2011-08-18T22:04:13Z)

## 2.6.2 データセットを作る
### del.icio.usからprogrammingタグの人気のURLをブックマークしたユーザを抜いてくる
    List(oherrmann,kislay.1990,penb,shayhazeles,raulhmacias)
    ritec: Map(http://openviz.wordpress.com/2011/09/08/indices-of-deprivation-linked-data-prototype/ -> 0.0, http://tirania.org/blog/archive/2011/Sep-06.html -> 1.0, http://code.google.com/p/zen-coding/ -> 0.0, http://merbist.com/2011/08/30/deploying-a-rails-3-1-app-gotchas/?utm_source=rubyweekly&utm_medium=email -> 0.0, http://developer.cisco.com/web/axl/forums/-/message_boards/message/1529729 -> 0.0, ...)
    nicholasdot: Map(http://openviz.wordpress.com/2011/09/08/indices-of-deprivation-linked-data-prototype/ -> 0.0, http://tirania.org/blog/archive/2011/Sep-06.html -> 0.0, http://code.google.com/p/zen-coding/ -> 0.0, http://merbist.com/2011/08/30/deploying-a-rails-3-1-app-gotchas/?utm_source=rubyweekly&utm_medium=email -> 0.0, http://developer.cisco.com/web/axl/forums/-/message_boards/message/1529729 -> 0.0, ...)
    Ode0n: Map(http://openviz.wordpress.com/2011/09/08/indices-of-deprivation-linked-data-prototype/ -> 0.0, http://tirania.org/blog/archive/2011/Sep-06.html -> 0.0, http://code.google.com/p/zen-coding/ -> 0.0, http://merbist.com/2011/08/30/deploying-a-rails-3-1-app-gotchas/?utm_source=rubyweekly&utm_medium=email -> 0.0, http://developer.cisco.com/web/axl/forums/-/message_boards/message/1529729 -> 0.0, ...)
    badgerjasso: Map(http://openviz.wordpress.com/2011/09/08/indices-of-deprivation-linked-data-prototype/ -> 0.0, http://tirania.org/blog/archive/2011/Sep-06.html -> 0.0, http://code.google.com/p/zen-coding/ -> 0.0, http://merbist.com/2011/08/30/deploying-a-rails-3-1-app-gotchas/?utm_source=rubyweekly&utm_medium=email -> 0.0, http://developer.cisco.com/web/axl/forums/-/message_boards/message/1529729 -> 0.0, ...)
    trinidadirishes: Map(http://openviz.wordpress.com/2011/09/08/indices-of-deprivation-linked-data-prototype/ -> 0.0, http://tirania.org/blog/archive/2011/Sep-06.html -> 0.0, http://code.google.com/p/zen-coding/ -> 0.0, http://merbist.com/2011/08/30/deploying-a-rails-3-1-app-gotchas/?utm_source=rubyweekly&utm_medium=email -> 0.0, http://developer.cisco.com/web/axl/forums/-/message_boards/message/1529729 -> 0.0, ...)

## 2.6.3 ご近所さんとリンクの推薦
### ユーザーに似た嗜好のユーザを探す
    ユーザ名: schillerrguffey
    List((0.4552380952380952,horanwinfield), (0.4552380952380952,dellhume), (0.42119047619047617,badgerjasso), (0.3871428571428571,vanghendricks), (0.3871428571428571,shayhazeles))
### ユーザが好みそうなリンクを探す
    List((0.4426884632098739,http://zoomzum.com/15-best-sites-to-download-free-web-templates/), (0.3681170683153028,https://market.android.com/details?id=com.glasses.tryout&pli=1), (0.3395368420781764,http://www.edutopia.org/blog/20-tips-new-teachers-lisa-dabbs), (0.3144294183723264,http://www.rehabinfo.net/blog/drug-addiction-signs/), (0.29180927215509334,http://www.cosmeticsurg.net/blog/2011/08/11/fda-stem-cell-regulation-and-the-english-language-switched-at-birth/))
### 特定のリンクに似たリンクを探す
    URL: http://zoomzum.com/15-best-sites-to-download-free-web-templates/
    List((0.6210923873952102,http://www.edutopia.org/blog/20-tips-new-teachers-lisa-dabbs), (0.5913123959890826,http://www.rehabinfo.net/blog/drug-addiction-signs/), (0.5848976518656018,http://www.tungstenaffinity.com/Tungsten-Ring-Buying-Guide-a/278.htm), (0.5229763603684907,http://fitvidsjs.com/), (0.4823819106188661,http://www.clearingthepicture.com/blog/perks-and-benefits-for-the-united-states-workers/))

## 2.7.1 アイテム間の類似度のデータセットを作る
    (Superman Returns,List((0.3090169943749474,Snakes on a Plane), (0.252650308587072,The Night Listener), (0.2402530733520421,Lady in the Water), (0.20799159651347807,Just My Luck), (0.1918253663634734,You, Me and Dupree)))
    (The Night Listener,List((0.38742588672279304,Lady in the Water), (0.32037724101704074,Snakes on a Plane), (0.2989350844248255,Just My Luck), (0.29429805508554946,You, Me and Dupree), (0.252650308587072,Superman Returns)))
    (Just My Luck,List((0.3483314773547883,Lady in the Water), (0.32037724101704074,You, Me and Dupree), (0.2989350844248255,The Night Listener), (0.2553967929896867,Snakes on a Plane), (0.20799159651347807,Superman Returns)))
    (You, Me and Dupree,List((0.4494897427831781,Lady in the Water), (0.32037724101704074,Just My Luck), (0.29429805508554946,The Night Listener), (0.1918253663634734,Superman Returns), (0.1886378647726465,Snakes on a Plane)))
    (Snakes on a Plane,List((0.3483314773547883,Lady in the Water), (0.32037724101704074,The Night Listener), (0.3090169943749474,Superman Returns), (0.2553967929896867,Just My Luck), (0.1886378647726465,You, Me and Dupree)))
    (Lady in the Water,List((0.4494897427831781,You, Me and Dupree), (0.38742588672279304,The Night Listener), (0.3483314773547883,Snakes on a Plane), (0.3483314773547883,Just My Luck), (0.2402530733520421,Superman Returns)))

## 2.7.2 推薦を行う
### アイテムベースの表からToby向け推薦を行う
    (3.1667425234070894,The Night Listener)
    (2.9366294028444355,Just My Luck)
    (2.8687673926264674,Lady in the Water)

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
### ピアソン相関でToby用の商品を推薦
    (3.3477895267131013,The Night Listener)
    (2.832549918264162,Lady in the Water)
    (2.530980703765565,Just My Luck)
### ユークリッド距離でToby用の商品を推薦
    (3.4571286944914226,The Night Listener)
    (2.778584003814924,Lady in the Water)
    (2.422482042361917,Just My Luck)
### Tanimoto係数でToby用の商品を推薦
    (3.428242075208925,The Night Listener)
    (2.7945514303777657,Lady in the Water)
    (2.3921287261885347,Just My Luck)

## 2.10.2 タグの類似性
### del.icio.usからprogrammingタグの人気のURLについているタグを抜いてくる
    List(plugin,reference,lists,read,workflow)
    reference: Map(http://ocw.mit.edu/OcwWeb/Electrical-Engineering-and-Computer-Science/6-00Fall-2008/CourseHome/index.htm -> 0.0, http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.webdesigndev.com/programming/30-most-influential-people-in-programming -> 0.0, http://hootsuite.com/hoot5 -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, ...)
    books: Map(http://ocw.mit.edu/OcwWeb/Electrical-Engineering-and-Computer-Science/6-00Fall-2008/CourseHome/index.htm -> 0.0, http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.webdesigndev.com/programming/30-most-influential-people-in-programming -> 0.0, http://hootsuite.com/hoot5 -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 1.0, ...)
    codecomplete: Map(http://ocw.mit.edu/OcwWeb/Electrical-Engineering-and-Computer-Science/6-00Fall-2008/CourseHome/index.htm -> 0.0, http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.webdesigndev.com/programming/30-most-influential-people-in-programming -> 0.0, http://hootsuite.com/hoot5 -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, ...)
    !tumblr-tech: Map(http://ocw.mit.edu/OcwWeb/Electrical-Engineering-and-Computer-Science/6-00Fall-2008/CourseHome/index.htm -> 0.0, http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.webdesigndev.com/programming/30-most-influential-people-in-programming -> 0.0, http://hootsuite.com/hoot5 -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, ...)
    development: Map(http://ocw.mit.edu/OcwWeb/Electrical-Engineering-and-Computer-Science/6-00Fall-2008/CourseHome/index.htm -> 0.0, http://www.spinellis.gr/blog/20100711/ -> 0.0, http://www.webdesigndev.com/programming/30-most-influential-people-in-programming -> 0.0, http://hootsuite.com/hoot5 -> 0.0, http://www.brainpickings.org/index.php/2011/06/13/10-primers-on-culture/ -> 0.0, ...)
### referenceに似たタグを探す
    List((0.2864904317813115,programming), (0.2754341213449502,tutorial), (0.24774471250539498,development), (0.22350193144354116,books), (0.19133011597053315,book))
### programmingタグのついていない似たリンクを探す
    (0.3792944814100043,https://github.com/bryanbibat/rails-3_0-tutorial)
    (0.31196276519941707,http://www.khanacademy.org/)
    (0.31196276519941707,http://www.jackkinsella.ie/2011/09/05/textmate-to-vim.html)
    (0.23140049713275113,http://skytechgeek.com/2011/09/6-useful-applications-for-mobile-development/)
    (0.2179161816649686,http://www.jessicahische.is/obsessedwiththeinternet/andhelpingyougetpaid/the-dark-art-of-pricing)

## 2.10.3 ユーザベースの効率化
### ユーザ間の類似度のデータセットを作る
    (Toby,List((0.4,Mick LaSalle), (0.38742588672279304,Michael Phillips), (0.3567891723253309,Claudia Puig), (0.3483314773547883,Lisa Rose), (0.2674788903885893,Jack Matthews), (0.25824569976124334,Gene Seymour)))
    (Michael Phillips,List((0.5358983848622454,Claudia Puig), (0.4721359549995794,Lisa Rose), (0.38742588672279304,Mick LaSalle), (0.38742588672279304,Toby), (0.3405424265831667,Gene Seymour), (0.32037724101704074,Jack Matthews)))
    (Gene Seymour,List((0.6666666666666666,Jack Matthews), (0.3405424265831667,Michael Phillips), (0.29429805508554946,Lisa Rose), (0.28172904669025317,Claudia Puig), (0.27792629762666365,Mick LaSalle), (0.25824569976124334,Toby)))
    (Jack Matthews,List((0.6666666666666666,Gene Seymour), (0.3405424265831667,Lisa Rose), (0.32037724101704074,Claudia Puig), (0.32037724101704074,Michael Phillips), (0.2857142857142857,Mick LaSalle), (0.2674788903885893,Toby)))
    (Claudia Puig,List((0.5358983848622454,Michael Phillips), (0.38742588672279304,Lisa Rose), (0.3567891723253309,Toby), (0.32037724101704074,Jack Matthews), (0.31451985913875646,Mick LaSalle), (0.28172904669025317,Gene Seymour)))
    (Mick LaSalle,List((0.4142135623730951,Lisa Rose), (0.4,Toby), (0.38742588672279304,Michael Phillips), (0.31451985913875646,Claudia Puig), (0.2857142857142857,Jack Matthews), (0.27792629762666365,Gene Seymour)))
    (Lisa Rose,List((0.4721359549995794,Michael Phillips), (0.4142135623730951,Mick LaSalle), (0.38742588672279304,Claudia Puig), (0.3483314773547883,Toby), (0.3405424265831667,Jack Matthews), (0.29429805508554946,Gene Seymour)))
### Lady in the Waterを見ていない人で高い評価をつけそうな人を
    (2.482581708640552,Michael Phillips)
    (2.422482042361917,Toby)
    (2.2030534980335528,Jack Matthews)

