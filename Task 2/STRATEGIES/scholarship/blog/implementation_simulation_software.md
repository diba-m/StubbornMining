# Implementierung Simulationssoftware
## Schwierigkeiten und Learnings bei der Implementierung der Simulationssoftware

Nach einer kleinen Odyssee habe ich anfangs 2017 endlich ein Thema für meine Diplomarbeit gefunden. Zusammen mit SBA-Research entschied ich mich "selfish mining" zu untersuchen, ein bekanntes Mining-Verhalten welches den relativen Ertrag des Miners gegenüber dem Rest des Netzwerkes erhöht. Dabei benützten wir eine neuartige Simulationsart, bei der wird das ganze peer-to-peer Netzwerk auf einem Host virtualisieren und ein vorkonfiguriertes Simulationsszenario ausführen. Diese Art und Weise der Simulation erlaubt uns die Netzwerklatenz natürlich in die Simulation mit einfließen zu lassen und zudem kann man einfach die Referenzimplementierung von Bitcoin ohne großen, zusätzlichen Mehraufwand wiederverwenden .

### Technologiestack

Als Technologie setzen wir dabei vor allem auf Docker zum Virtualisieren des Netzwerkes und auf Python um die Simulation zu orchestrieren. Die Referenzimplementierung von Bitcoin wird mehrfach in sogenannten Containern gestartet und die einzelnen Container bilden dann zusammen ein peer-to-peer Netzwerk, indem sie sich untereinander verbinden. Die Implementierung dieser Simulationssoftware nahm gut drei Monate in Anspruch und es traten einige unerwartete Schwierigkeiten auf. Vielen Dank an dieser Stelle auch an Andreas Kern, der mich bei der Implementierung tatkräftig unterstützt hat. Er wird seine Bachelorarbeit auf die Simulationssoftware aufbauen.

### Größtes Problem?

Das größte Problem bildete sicherlich ein sehr merkwürdiges Verhalten der einzelnen Bitcoin-Nodes. Zu diesem Zeitpunkt war die Implementierung schon weit fortgeschritten und es war schon möglich Simulationsszenarien zu starten. Jedoch verursachten die einzelnen Nodes nach einiger Laufzeit eine sehr hohe Auslastung der CPU. Wir hatten verschiedenste Vermutung zur Ursache dieses Problems und implementierten eine Performanceverbesserung nach der anderen. Das Problem blieb jedoch bestehen. Einige schlaflose Nächte und viele italienische Espressi später rang ich mich endlich durch ein Tool zu verwenden, welches die Bitcoin-Referenzimplementierung während der Laufzeit beobachtet. Das Tool machte mich sofort auf eine Funktion im Bitcoin-Code aufmerksam, welche mit folgendem Kommentar versehen ist:  

```
// Sanity checks off by default for performance, because otherwise
// accepting transactions becomes O(N^2) where N is the number
// of transactions in the pool
```

Ich griff mir gleich an den Kopf und ein Block äh Stein fiel mir vom Herzen. Die Funktion ist normalerweise aus Performancegründen deaktiviert, da die Laufzeit der Funktion quadratisch ist. Ich verwende jedoch die Software in einem speziellem Modus. Dadurch ist die Methode aktiv und verursacht die hohe Auslastung der CPU am Host. Die Lösung des Problems war dann die Referenzimplementierung mit dem zusätzlichen Flag "--mempoolcheck 0" zu starten, welches dann die Methode deaktivierte.

### Learnings

Erstens: Hast du ein Problem, solltest du immer genau wissen, was die Ursache ist, und nicht irgendwelche im Endeffekt wirkungslose Verbesserungen durchführen. Packe das Problem an der Wurzel!

Zweitens: Schrecke nicht vor unbekannten Thematiken zurück! In meinem Fall war es das mir unbekannte Tool. Nach ein bisschen Einlesen und Herumspielen konnte ich das Tool verwenden und die Ursache des Problems war im Nu gefunden.

Nun, da die Simulationssoftware funktioniert, kann ich mich ans Simulieren ran machen. Stay tuned! 
