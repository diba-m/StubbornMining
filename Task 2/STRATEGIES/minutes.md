# 7.12.16
* mit klemen
* kurze vorstellung meinerseits, spezialisierungsbereiche, ideen
* mögliche themenbereiche
 * secure software development lifecycle (secure SDLC)
 * trusted code
 * mitarbeit bei einer security matrix (x-achse programmiersprache, y-achse typischer themenbereich)
 * blockchain, e-voting
* forschungsantrag TRUC
* folgetreffen mit weipl

# 18.01.17
* mit weipl
* themenbereiche bei blockchain
 * namecoin
 * merged mining
* dissertanten bei SBA
 * Nicholas Stifter
 * Aljosha Judmayer
* machstrukturen im bitcoin netzwerk
* was passiert wenn mining reward = 0
* migrationen von blockchains

# 18.01.17
* mit aljosha
* simulation
* momentan problem bei simulatio mit zfs laufwerk
* erstellen von accounts (gitlab, mattermost, ...)
* freischalten von repo mit wichtigen papers
* next steps
 * grundlegende papers lesen
 * thema überlegen

# 09.02.17
* mit aljosha, nicholas, andreas
* zfs thema besteht immer noch, erster task von simon
* ideen für DA
 * selfish mining (wird priorisiert)
 * eclipse attacks
* kleine recherche und einlesen in netzwerk simulations software (ns2, ns3)
* slicetime, als input wie man netzwerk simulation auch umsetzen kann
* simulation soll ohne großen aufwand auf ähnlichem oder stärkerm system reproduzierbar und deterministisch wiederholbar sein
* mit andreas zusammen setzen, nächstes meeting mit aljosha anfang märz

# 15.02.17
* mit andreas
* einführung und Q&A für simon
* andreas refactored den code weiter
 * DockerCommands
 * NetworkCommands
 * LogProcessing
 * lesbarkeit, naming
 * string concatenation besser lesbar machen
* btn = bitcoin testnetwork
* simon lest sich weiter ein
 * im bitcoin wiki werden viele befehle erklärt, client mal mit den commands ansprechen und system weiter verstehen
 * zfs reproduzieren und schauen ob problem besteht
* auch commands in den ELK stack füttern für eventuelles troubleshooting
* selfish minig könnte man mit einem proxy umsetzten der zwischen 2 nodes sitzt networkt <-> normal node <-> proxy, and only connections to <-> normal node with known IP
im proxy wird das selfish mining implementiert somit müsste man die referenz implementierungen nicht angreifen

# 06.03.17
* mit aljosha, nicholas, andreas
* erklärung und besprechung zfs
 * zfs funktioniert
 * zfs am host system hat dedup und recordsize 512bytes bereits aktiviert
* virtual machine (vm) mit simulation ausreizen und ertesten wo das bottle neck ist
* vm kann noch skaliert werden und notfalls können simulationen auch in der cloud ausgeführt werden
* simon implementiert proxy node
 * lesen der selfish mining papers mit den verschiedenen strategien
 * implementierung in python
 * so generisch wie möglich dh. verschieden selfish mining strategien und der proxy soll auch als eclipse attack node verwendet werden können
 * aljosha schickt relevante selfish mining papers als ausgangslage
* andreas refactored und implementiert simcoin für seine arbeit weiter

# 08.06.17
* mit aljosha, nicholas
* AMS bestätigung des fortschrittes für bildungskarenz benötigt
* erklärung/einführung proxy
 * kein public bitcoind node mehr, da sonst die match aktion des selfish mining algorithmus nicht möglich wäre
 * da kein public bitcoind node mehr vorhanden ist, musste viel vom netzwerk stack implementiert werden
 * strategie programmatisch mit vielen if/else umgesetzt
 * proxy hat eine eigene view der blockchain
* erklärung/einführung simcoin
 * viele bash commands die mit python erstellt werden
 * leicht unübersichtlich, schlecht getestet und schlecht lesbar
 * auswertung in CSV files (chain in nodes, blocks, consensus chain)
 * start-up tricky. 100 + #nodes an blöcke erstellen damit jeder node geld hat
 * delays mit tc. leider noch ein problem wenn eine verbindung delayed werden soll und alle anderen nicht, zB für den proxy node. simon schickt an aljosha ein beispiel code zum reproduzieren des problems.
* next steps
 * implementierung von ticks (tick.config)
      * pro zeile ein tick
      * ein tick beschreibt einen zeitabschnitt
      * in einem tick kann ein node 0-1 events (tx oder block) ausführen
      * in einem tick können 0-n events sein
      * nachdem alle events ausgeführt wurden, wird bis zum nächsten tick gewartet
 * implementiereung der netzwertopologie (network.config)
      * in einer matrix werden die verbindungen zwischen den nodes mitsamt delay angegeben
 * beide files (tick. und network.config) werden von einem neuen python script erstellt
 * proposal
      * nach eigenem ermessen schreiben
      * vorlagen und beispiel proposals sind im www zu finden

# 29.06.17
* mit aljosha
* AMS wurde verlängert
* erklärung/einführung network.py und tick.py
 * die beiden programme dienen zu erstellen der configurationsdateien network.config und ticks.csv
 * passen im großen und ganzem so, leichte anpassungen vl notwendig
* vorstellen der ersten version des proposals
 * DES-simulation unpassend, nur simulation verwenden
 * vorteil von simulation besser hervorheben
* förderung durch netidee interessant
 * vorerst aber proposal fertig schreiben
 * deadline für netidee am 20. juli
* falls netidee nicht klappt eventuell durch bitcoin community fördern lassen
* next steps
 * simon kümmert sich um die simulation
      * berechnung der block stale rate
      * berechnung der durchschnittlichen block propagation time
      * berechnung der durchschnittlichen tx propagation time
      * tracking des tx backlog
      * mehrmaliges simulieren (ohne selfish mining) und vergleichen mit https://allquantor.at/blockchainbib/pdf/decker2013information.pdf und https://allquantor.at/blockchainbib/pdf/gervais2016security.pdf
 * aljosha gibt feedback zum proposal
 * danach fertigstellung des proposals durch simon

# 12.07.17
* mit aljosha
* besprechung der netidee bewerbung
* input für einzlene fragen
* dreh von aljosha's video part
* next steps
 * bewerbung deadline 20. juli
      * erstellen von empfehlungsschreiben von edgar
      * fertigstellen aller fragen
      * korrektur lesen aller fragen
      * fertigstellen des videos
 * beginnen mit simulation

# 25.07.17
* mit aljosha, nicholas und andreas
* netidee eingereicht
 * entscheidung september/august
* performance
 * tx und block generation muss schneller funktionieren
 * problem identifizieren und verbesseren
* andreas beteiligt sich wieder, ab jetzt mit dem feature issues im projekt arbeiten
* ende september technical report für cryptofinancials
* intervals in ticks umbennen
* simulation
 * mindestens 20 miner verwenden
 * tick-duration immer weiter reduzieren
 * latency immer weiter erhöhen
 * simulieren, simulieren, simulieren und auf resultate hoffen
* ausblick für die zukunft
 * mehrere docker networks verwenden
 * latency mit tc command besser konfigurieren

# 06.10.17
* mit aljosha und andreas
* ZFS performance
 * simulation die im ordner /blockchain gestartet wird, bricht nach einiger zeit ab
 * ZFS bringt speicher effizienz, jedoch bremst die performance
 * in der zukunft wird ZFS deaktiviert
* RPC-performance
 * peter todd python-bitcoinlib verwendet normale python TCP connections
 * umstellen auf UNIX domain sockets nicht möglich da diese noch nicht implementiert sind
      * https://github.com/bitcoin/bitcoin/pull/9919
      * https://github.com/bitcoin/bitcoin/pull/9979
* determinismus der simulation zeigen - nicht genauer besprochen
* simulation spiegelt bitcoin wieder - wird sehr schwierig zu argumentieren
* selfish mining - nicht besprochen
* einreichen bitcoin workshop vorerst auf eis
