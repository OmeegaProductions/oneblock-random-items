# OneBlock Random Items

Un plugin minecraft pour 26.3 avec deux modes de jeu basés sur les drop random.

## Installation
1. Téléchargez le plugin
2. Placer le .jar dans le dossier "plugins" du serveur
3. Relancer le serveur

## Modes de jeu
### Coopération
Mode de jeu par défaut, le but du jeu est de tuer l'ender dragon ! Respawn infini, un seul but : récolter les morceaux de portail pour aller dans l'end.

### Duel
Mode de combat, chaque joueur spawn sur un bloc de bedrock en cercle, le dernier en vie gagne la partie.

## Commandes
### /config
Permet de changer les configurations du plugin :
- /config mode cooperation => Passe en mode "coopération"
- /config mode duel => Passe en mode "duel"
- /config interval <X> => Permet de changer l'intervalle entre deux drops (au moins 0.05)
- /config distance <X> => Permet de changer la distance entre chaque bloc du cercle en mode "duel"
- /config startItems <X> => Permet de configurer le nombre d'items donnés instantanément au démarrage de la partie

### /game
- /game start => Lance la partie
- /game reset => Réinitialise la partie

Si une partie "coopération" est en cours et que le serveur est éteint, la partie reprend au démarrage.
