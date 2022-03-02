# Projet 7 - Application [Go4lunch](https://github.com/gtdevgit/P7)

Application développée en **java** avec **Android Studio**.

![Android](https://img.shields.io/badge/Android-Studio-blue)
![git](https://img.shields.io/github/languages/code-size/gtdevgit/P7)

***Trouvez un restaurant pour déjeuner avec vos collègues***

### Principale fonctionnalitées
***

>[Consulter la documentation fonctionnelles complete au format pdf.](https://github.com/gtdevgit/P7/blob/main/Documentation/P7_Documentation%20fonctionnelle.pdf)

- **Authentifier l’utilisateur** avec un compte
  - Google
  - Facebook
  - Twitter
- Permettre à l’utilisateur de **se déconnecter** et de supprimer son compte.
- Afficher une **carte** avec la position de l’utilisateur et les restaurants situés autour de l’utilisateur
- **Choisir** un restaurant pour le midi
- **Liker** un restaurant
- Afficher la **liste** des restaurants situés autour de l’utilisateur
  - Indiquer la distance entre l’utilisateur et les restaurants
  - Afficher le nombre de like d’un restaurant
  - Afficher le nombre de collègues ayant choisi un restaurant
- Consulter le **détail** d’un restaurant
  - Adresse
  - Photo
  - Horaire
  - Notation
  - Lien vers le site web
  - Téléphoner au restaurant
- Afficher la liste des **collègues**
  - Afficher le choix des collègues
- **Notifications**
  - Recevoir une notification de rappel
  - Ouvrir l’application depuis les notifications

### Eléments techniques
***
>[Consulter la documentation techniques détaillée au format pdf.](https://github.com/gtdevgit/P7/blob/main/Documentation/P7_Documentation%20technique.pdf)

L’application a été développée pour **Android** avec **Android Studio**.

Le langage utilisé est le **Java**.

Elle supporte **Android 4.4 Kitkat**.

Les sources de l’application sont hébergées sur **Github**

> https://github.com/gtdevgit/P7

### Service tiers :

- L'Autentification est gérée par **Google Firebase**. Elle autorise les comptes : *Google*, *Facebook* et *Twitter*.

- La base de données est une base de données *No SQL* **Google Firestore**.

- L'application reqiére une **cle d'API** Google qui doit être intégrée dans le fichier *gradle.property*.

- **Geolocation** obtenue avec *FusedLocationProviderClient*.

- **Autorisation** *ACCESS_FINE_LOCATION* nécessaire.

- **Affichage de la Map** avec *Google Map SDK*.

- Les **informations sur les restaurants** sont fournis pas *Google Place API*.

- Les échanges *http* sont réalisés avec **retrofit**.

- Le débuggae *http* peut être fait avec **OKHttpClient**.

- Serialisation *json* avec **Gson**.

### Design

- Le design générale de l'application est géré dans un **Navigation Drawer**.

- Les vues sont gérés par des **fragments**.

### Architecture

L'application utilise **Android Architecture Components** et les patrons de conception :

- *MVVM*
- *Singleton* pour la fabrique des ViewModel
- *Observer* pour dialogue entre les couche applicatives

Utilisation des classes :

- ViewModel,
- ViewModelProvider.Factory,
- MutableLiveData,
- LiveData,
- Transformation,
- MediatorLiveData.

Notification : *NotificationWorker*

Tests unitaires réalisées avec **Junit**

Tests des view modèles réalisés avec **Mokito** et *l'injection de dépendance*
