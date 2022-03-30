# PM3-FS22-IT20ta_zh-Team4-pretty_secure_cloud
Pretty Secure and pretty :)

## Branching-Modell

Die Entwickler erstellen für jedes Issue einen Feature-Branch direkt vom dev-branch.
Dabei wird das folgende Naming eingehalten: `featture/<branch name>`. Ausserdem wird für
Dokumentationen ein eigenes Branch eröffnet. Dabei wird das folgende Naming eingehalten: `doc/<branch name>`

Bevor ein Pull-Request vom Entwickler erstellt wird, müssen die folgenden Bedingungen erfüllt sein:

* Issue ist vollständig umgesetzt (in Ausnahmefällen mit `todo`)
* Clean-Code Regeln wurden eingehalten, Imports optimiert und Code formatiert (durch Intellij)
* Code ist kompilierbar

Nachdem der Pull-Request von mindestens einem Code-Owner approved wurde, wird der Feature-Branch vom Ersteller
selbstständig in den dev-branch gemerged. Der Feature-Branch oder der Doc-Branch wird im Anschluss gelöscht.

![Branching Modell](doc/branching_modell.png)

## Arbeiten mit Git
1.) Kontrolle ob man die Aktuellste Version hat `Git update` oder `Fetch`

2.) Local seinen Branch erstellen. Passend zum Issue den Branch Name wählen

3.) Bevor man seine Änderungen commitet, nochmals kontrollieren, dass man die Aktuellste Version hat 
     ggf. mergen und wenn nötig Anpassungen vornehmen

4.) Für die commit message kurze und treffende Beschreibung geben was geändert wurde

5.) Pushen auf git

6.) Pull-request erstellen auf GitHub

7.) Nach dem der Code approved wurde eigenen Branch mergen

8.) Branch löschen auf GitHub

