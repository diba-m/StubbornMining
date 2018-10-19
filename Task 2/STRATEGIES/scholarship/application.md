# 2.1 Kurzbeschreibung der Arbeit

Die Forschungsfrage der Arbeit bezieht sich auf den sogenannten Selfish-Mining-Angriff in Bitcoin. Dabei werden verschiedene Arten des Selfish-Minings simuliert und dem normalen, honest Mining gegenübergestellt. Im Gegensatz zur bisherigen Forschung wird bei dieser Simulierung Latenz im Peer-to-Peer-Netzwerk durch Simulierung des Netzwerkes berücksichtigt. Dies ermöglicht eine realistischere Simulierung, welche Verzögerung und natürliche Forks im Bitcoin-Netzwerk inkludiert. Zusätzlich kann in der Simulierung die Referenzimplementierung von Bitcoin direkt verwendet werden. Dadurch ist keine zusätzliche oder adapierterte Implementierung der Referenzimplementierung nötig und alle Eigenschaften des Bitcoin-Protokolls werden automatisch berücksichtigt. Neben der Analyses des Angriffes ist die Simulationssoftware an sich ein weiterer Fokus der Arbeit. Durch die neuartige Simulation können auch andere Angriffe und auch Modifikationen des Protokolls analysiert und getestet werden.

# 2.2 Forschungsstand zum Thema

Im Jahre 2014 wurde der Selfish-Mining-Angriff von Eyal und Sirer formalisiert. Die beiden konnten beweisen, dass ein Miner bei einer Attacke mehr als seinen fairen Anteil der Mining-Rewards erhält. Durch geschicktes Zurückhalten von selbst gefunden Blöcken schafft es der Miner, dass andere Miner ihre Leistung für unnütze Blöcke verschwenden. Dadurch steigert der Selfish-Miner seinen relativen Anteil an Rechenleistung und damit auch den relativen Anteil an Mining-Rewards. Spätere Forschung ergaben verschiedene Variationen des Selfish-Minings welche sich, abhängig von Konnektivität und Rechenleistung des Selfish-Miners, als noch erfolgreicher erwiesen. Zum Beweisen des Selfish-Mining-Angriffs wurden verschiedenste Methoden verwendet. Aufbauend auf einem Zustandsautomat simulierten Forscher die möglichen Pfade oder bestimmten mittels MDPs die optimale Strategie des Selfish-Miners. Auch die Prävention eines solchen Angriffs ist Gegenstand der Forschung.

# 2.3 Ziele der Arbeit

Ziel der Arbeit ist eine akkuratere Simulierung verschiedener Selfish-Mining-Strategien und daraus folgend ein besseres Verständnis der Implikationen eines solchen Angriffs. Die Selfish-Mining-Strategien umfassen: - selfish mining - lead stubborn mining - trail stubborn mining - equal-fork stubborn mining Durch die Simulation kann für eine bestimmte Verteilung der Rechenleistung und Netzwerktopologie die jeweils beste Strategie ermittelt werden. Die Ergebnisse der Simulation sollten den momentanen Forschungsstand untermauern, beziehungsweise neue Einsichten bezüglich des Einflusses der Netzwerktopologie auf Selfish-Mining liefern. Ein zusätzlicher Output der Arbeit ist die Simulationssoftware Simcoin, welche eine deterministische Simulation von Blockchain-Technologie erlaubt. Hierbei ist es möglich, die Referenzimplementierung direkt zu verwenden und das Netzwerk realistisch zu simulieren. Im Rahmen der Förderung ist ein zusätzliches Aufbereiten und Erweitern von Simcoin geplant.

# 2.4 Angewandte Methode

Im ersten Schritt werden die Mining-Strategien selfish, lead stubborn, trail stubborn, und equal-fork stubborn Mining implementiert. Mithilfe des proxy Design-Patterns können die Mining-Strategien losgelöst von der Referenzimplementierung umgesetzt werden. Im zweiten Schritt wird die Simulationssoftware Simcoin implementiert. Dabei wird der rechenintensive Proof-of-Work deaktiviert und die Simulationssoftware entscheidet selbst, welcher Node wann einen Block findet. Ähnlich dazu wird auch die Netzwerktopologie und Netzwerklatenz zentral von der Simulationssoftware festgelegt. Nach der Implementierung der zwei Komponenten werden die verschiedenen Selfish-Mining-Strategien simuliert. Die Resultate der Simulation werden anschließend verglichen und dem normalen, honest Mining gegenübergestellt.

# 2.5 Grober Zeitplan

* 1. März - 30. Mai -- Recherche, Einlesen Literatur (erledigt) 
* 1. Juni - 30. Juni -- Implementierung der verschiedenen Selfish-Mining-Strategien (erledigt) 
* 1. Juli - 10. Juli -- Erstellung eines Simcoin Prototyps (erledigt) 
Bis 20. Juli -- Einreichen des Proposals (erledigt) 
* 11. Juli - 10. August -- Fertigstellung aller Implementierungen
* 11. August - 31. August -- Simulation und Evaluation
* 1. September - 30. November -- Fertigstellung der Diplomarbeit (Kapitel für Kapitel zeitgleich Abstimmung und Korrekturlesen
* 1. Oktober - 31. Oktober -- Optimierung und Refactoring von Simcoin
* 1. November - 30. November -- Code-Review und Vorbereitungen zum Open-sourcen von Simcoin
* 1. Dezember -- Veröffentlichung von Simcoin
* 7. Dezember -- Einreichfrist Diplomarbeit an der TU Wien

# 2.6 Geplante Fertigstellung

Dez 2017

# 2.7 Wesentliche Literatur

* Nakamoto, S. (2008). Bitcoin: A peer-to-peer electronic cash system. Eyal, I., & Sirer, E. G. (2014, March).
* Majority is not enough: Bitcoin mining is vulnerable. In International conference on financial cryptography and data security (pp. 436-454). Heidelberg, Berlin: Springer. Nayak, K., Kumar, S., Miller, A., & Shi, E. (2016, March).
* Stubborn mining: Generalizing selfish mining and combining with an eclipse attack. In Security and Privacy (EuroS&P), 2016 IEEE Symposium on (pp. 305-320). IEEE. Sapirshtein, A., Sompolinsky, Y., & Zohar, A. (2016, February).
* Optimal selfish mining strategies in bitcoin. In International Conference on Financial Cryptography and Data Security (pp. 515-532). Heidelberg, Berlin: Springer. Gervais, A., Karame, G. O., Wüst, K., Glykantzis, V., Ritzdorf, H., & Capkun, S. (2016, October).
* On the security and performance of proof of work blockchains. In: Proceedings of the 2016 ACM SIGSAC Conference on Computer and Communications Security (pp. 3-16). ACM.

# 2.8 Kurzer Lebenslauf der Antragstellerin / des Antragstellers

-- REMOVED --

# 2.9 Video
WICHTIG: Im Video sollen vor allem (> 80%) die federführenden Projektakteure SELBST das zentrale Projektziel mit dem „Unique Selling Point“, also der Besonderheit gegenüber bereits vorhandenen Lösungen, darstellen. KEINE Animationen! Der Sinn des Videos ist, die Personen hinter dem Projekt kennen zu lernen! Bereitstellung des Videos über YOUTUBE.

https://youtu.be/ed4d6ItirP4

# 4.1 Relevanz der Arbeit für die Internet-Community in Österreich

Obwohl medial unterrepräsentiert, hat Österreich, auch im internationalen Vergleich, eine aktive und lebhafte Cryptocurrency- und Blockchain-Community. Grund dafür ist vermutlich eine Kombination aus Early-Adoptern, die geographische Nähe zum Crypto-Valley Zug in der Schweiz und die etwas liberalere Gesetzgebung im Vergleich zu anderen europäischen Nachbarländern. Diese Arbeit leistet nicht nur einen substanziellen Beitrag zu aktuellen Forschungsschwerpunkten im Bereich der Skalierbarkeit und Sicherheit von Blockchains, sondern zusätzlich profitieren heimische Cryptocurrency-Unternehmen und Start-Ups von dem gewonnen Know-How und Wissenstransfer in die heimische Community.

# 4.2 So wird das Ergebnis der Öffentlichkeit zur Verfügung gestellt

Die Diplomarbeit selbst wird über die TU Wien publiziert. Neben der Diplomarbeit werden auch alle Softwarekomponenten sowie die Dokumentation, über die Plattform github der Allgemeinheit zur Verfügung gestellt. Zusätzlich wird die Arbeit zusammen mit der Software der österreichischen Community bei Blockchain-Meetups vorgestellt und näher gebracht.

# 4.3 Art der creative commons / open source / Verwertung des Projekts

Geben Sie hier jene Lizenz(en) an, unter der/denen Sie Ihre Projektergebnisse der Öffentlichkeit zur möglichst uneingeschränkten weiteren Verwendung inkl. Weiterentwicklung zur Verfügung stellen werden.
ACHTUNG: Bitte beachten Sie die diesbezüglichen Bestimmungen im Dokument „netidee Call 11 – Richtlinien und Bedingungen“ sowie die diesbezüglichen FAQ, beides auf www.netidee.at.
WICHTIG: Listen Sie ALLE Projektergebnisse konkret auf (sinnvolle Granularität, max. 10 Punkte), die nach Projektabschluss für Dritte/die Öffentlichkeit zur Verfügung gestellt werden: SW, HW, Content, Dokumentation für Anwender / für Entwickler.

Dokument Diplomarbeit: Österreichisches Urheberrecht (sollte diese Art der Lizenz nicht Ihren Vorstellung entsprechen, bitten ich Sie mich zu kontaktieren) Software Selfish-Mining proxy: MIT Simcoin mitsamt Dokumentation: MIT

# 4.4 Ergeben sich wirtschaftliche / wissenschaftlich interessante Kooperationen?

Die Diplomarbeit wird in einer wissenschaftlichen Kooperation zwischen der Technischen Universität Wien und dem privaten Forschungszentrum SBA Research gGmbH durchgeführt. Dadurch ist eine Einbettung in die internationale Forschungslandschaft gegeben. Zudem wird die Kooperation in diesem Forschungsfeld in Zukunft fortgesetzt und intensiviert.

# 4.5 Ergeben sich beachtenswerte soziale, gesellschaftliche bzw. Umwelt-Aspekte?

Blockchain-Technologien haben einen disruptiven Charakter und können einen noch nicht absehbaren Einfluss auf gesellschaftliche und soziale Aspekte haben. Um diese Einflüsse besser abschätzen zu können, ist es wichtig, diese Technologien gut zu verstehen. Neben dem Behandeln der Forschungsfrage liefert die Diplomarbeit als zusätzliches Ergebnis eine Simulationssoftware. Die Software kann dazu benutzt werden, sichere und skalierbarer Versionen von Cryptocurrencies zu testen und zu verifizieren.

# 4.6 Bildet die Arbeit eine Grundlage für weitere Forschungen?

Durch die neuartige Simulation fließt die natürliche Netzwerklatenz direkt in die Simulation mit ein. Dies wird neue Einsichten in Selfish-Mining bringen und Grundlage für weitere Forschung sein. Weiters stellt die Simulationssoftware Simcoin eine neue und bessere Art der Simulierung dar. Sie bildet somit eine wichtige Grundlage für zukünftige Forschung im Bereich der Performance- und Sicherheitsverbesserungen von Cryptocurrencies.

# 4.7 Haben Sie vor, sich dem Forschungsthema nach Beendigung der Arbeit weiter zu widmen? Wenn ja, in welcher Form?

Nach dem Abschluss der Arbeit werde ich mich weiterhin mit DLT- und Blockchain-Technologien beschäftigen und werde mich in diesem Bereich für einen Forschungsplatz oder eine Arbeit bewerben. Ein großes Anliegen ist mir auch die Simulationssoftware Simcoin. Diese möchte ich im Rahmen neuer Forschungen und Simulationen zusammen mit der Blockchain-Community weiterentwickeln und verbessern.
