#  WashFlow — Gestion d'une Station de Lavage Auto

Application de bureau JavaFX connectée à une base de données MySQL, permettant de
gérer une station de lavage automobile : véhicules, lavages, statistiques et export
de données.
Projet réalisé dans le cadre du mini-projet **JavaFX — GI3 ENSAO** (Développement Java / IHM).


##  Description du sujet
L'application gère deux entités principales :
- **Véhicule** : informations sur les voitures enregistrées dans la station.
- **Lavage** : opérations de lavage effectuées, liées à un véhicule.
Ce domaine a été choisi car il est original, concret, et permet de couvrir naturellement
l'ensemble des contrôles JavaFX exigés (formulaires, tableau de bord, statistiques, export...).



## Architecture

```
src/main/java/
├── application/      → MainApp.java (point d'entrée + navigation)
├── controleur/        → Contrôleurs JavaFX (un par vue FXML)
├── dao/                → Accès aux données (DatabaseConnection + DAO par entité)
├── modele/             → Classes représentant les entités (Vehicule, Lavage, Utilisateur)
└── utils/              → Classes utilitaires (export CSV)

src/main/resources/vue/
├── Login.fxml
├── GestionVehicule.fxml
├── GestionLavage.fxml
├── Dashboard.fxml
└── Style.css
```

Architecture **MVC + DAO** : les contrôleurs gèrent l'interface, les DAO gèrent le SQL,
les modèles représentent les données.



##  Prérequis

- Java JDK 17 ou supérieur
- Maven 3.8+
- MySQL (ou WAMP/XAMPP avec MySQL actif sur le port 3306)



##  Installation de la base de données

1. Démarrer votre serveur MySQL (ex : via WAMP).
2. Importer le script SQL fourni :


mysql -u root -p < init_db.sql


Ou via phpMyAdmin : créer une nouvelle base et importer le fichier "init_db.sql".

3. Adapter si besoin les identifiants de connexion dans :
   `src/main/java/dao/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/station_lavage?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";
```



##  Lancer l'application

```bash
mvn clean javafx:run
```



##  Identifiants de connexion (Login)

| Username | Password | Rôle    |
|----------|----------|---------|
| admin    | admin123 | admin   |
| employe  | employe123 | employe |



##  Fonctionnalités

-  CRUD complet sur les véhicules (avec propriétaire : nom, prénom, téléphone) et les lavages
-  Recherche et filtrage en temps réel
- Tableau de bord avec navigation par sections (KPIs Financiers, Indicateurs Opérationnels,
  Répartition des Lavages, Évolution Mensuelle, Export & Personnalisation)
-  Export des données en CSV (FileChooser)
-  Authentification via base MySQL
-  Boîte de dialogue "À propos" (Aide → À propos)
-  Personnalisation de la couleur d'accent (ColorPicker)
-  Interface cohérente utilisant l'ensemble des contrôles JavaFX requis
  (TextField, TextArea, ComboBox, RadioButton, CheckBox, DatePicker, Spinner,
  Slider, ListView, TableView, ProgressBar, Tooltip, Alert, Accordion, MenuBar,
  ColorPicker...)



## Vidéo de démonstration

Lien Google Drive : https://drive.google.com/file/d/1z7wAzlYVPFXtgUAbYDiew6p1KXYk44jV/view?usp=sharing


##  Auteurs

Binôme :

El Youncha Malak -- El Youbi Chorouk


— GI3 ENSAO — 2025/2026
