package me.sergidalmau.cheflink.ui.util

import androidx.compose.runtime.staticCompositionLocalOf

interface Strings {
    val appTitle: String
    val tables: String
    val orders: String
    val settings: String
    val logout: String
    val login: String
    val username: String
    val password: String
    val loginError: String
    val connecting: String
    val initializing: String
    val serverNotFound: String
    val connectionDetails: (String) -> String
    val checkServerStatus: String
    val retry: String
    val table: String
    val tablesTitle: String
    val allTables: String
    val free: String
    val occupied: String
    val paxs: String
    val orderTitle: String
    val orderSummary: String
    val emptyOrder: String
    val generalNotes: String
    val total: String
    val send: String
    val close: String
    val cancel: String
    val addNoteTo: String
    val note: String
    val addNote: String
    val noDescription: String
    val filterByTable: String
    val filterByTableDesc: String
    val filterByStatus: String
    val allStatuses: String
    val numStatuses: (Int) -> String
    val deleteOrder: String
    val deleteOrderConfirm: String
    val delete: String
    val waiter: String
    val notes: String
    val noOrders: String
    val statusPending: String
    val statusPreparing: String
    val statusReady: String
    val statusSent: String
    val statusServed: String
    val statusCancelled: String
    val changePassword: String
    val newUser: String
    val notifications: String
    val notificationsDesc: String
    val sound: String
    val soundDesc: String
    val darkMode: String
    val darkModeDesc: String
    val autoPrint: String
    val autoPrintDesc: String
    val appInfo: String
    val version: String
    val lastUpdate: String
    val developedBy: String
    val syncData: String
    val clearCache: String
    val firstName: String
    val lastName: String
    val email: String
    val userRole: String
    val roleWaiter: String
    val roleAdmin: String
    val register: String
    val language: String
    val editModeActive: String
    val addTable: String
    val deleteTable: String
    val deleteTableConfirm: (Int) -> String
    val tableNumber: String
    val tableCapacity: String
    val tableAlreadyExists: String
    val save: String
    val editMode: String
    val exitEditMode: String
    val cannotDeleteTableWithOrders: String
    val componentSize: String
    val sizeSmall: String
    val sizeMedium: String
    val sizeLarge: String
    val articles: String
    val addArticle: String
    val editArticle: String
    val deleteArticle: String
    val articleName: String
    val articlePrice: String
    val articleCategory: String
    val articleDescription: String
    val categoryPrimers: String
    val categorySegons: String
    val categoryPostres: String
    val categoryBegudes: String
    val categoryMenus: String
    val articleAvailable: String
    val outOfStock: String
    val networkConfig: String
    val serverUrl: String
    val autoDiscover: String
    val discovering: String
    val internalServer: String
    val internalServerDesc: String
    val restartRequired: String
    val selectMode: String
    val modeHost: String
    val modeHostDesc: String
    val modeClient: String
    val modeClientDesc: String
    val startingServer: String
    val serverStartError: String
}

object CatalanStrings : Strings {
    override val appTitle = "ChefLink"
    override val tables = "Taules"
    override val orders = "Comandes"
    override val settings = "Configuració"
    override val logout = "Sortir"
    override val login = "Iniciar sessió"
    override val username = "Nom d'usuari"
    override val password = "Contrasenya"
    override val loginError = "Credencials invàlides. Torna-ho a provar."
    override val connecting = "Connectant amb el servidor..."
    override val initializing = "Iniciant ChefLink..."
    override val serverNotFound = "No s'ha pogut trobar el servidor"
    override val connectionDetails = { detail: String -> "Detall: $detail" }
    override val checkServerStatus = "Comprova que el servidor estigui engegat i la xarxa sigui correcte."
    override val retry = "Reintentar"
    override val table = "Taula"
    override val tablesTitle = "Taules"
    override val allTables = "Totes les taules"
    override val free = "Lliure"
    override val occupied = "Ocupada"
    override val paxs = "paxs"
    override val orderTitle = "Comanda"
    override val orderSummary = "Resum de la Comanda"
    override val emptyOrder = "La comanda està buida"
    override val generalNotes = "Notes generals"
    override val total = "Total"
    override val send = "Enviar"
    override val close = "Tancar"
    override val cancel = "Cancel·lar"
    override val addNoteTo = "Afegir nota a"
    override val note = "Nota"
    override val addNote = "Afegir nota"
    override val noDescription = "Sense descripció disponible"
    override val filterByTable = "Filtrar per Taula"
    override val filterByTableDesc = "Selecciona una taula per veure només les seves comandes."
    override val filterByStatus = "Filtrar per Estat"
    override val allStatuses = "Tots els estats"
    override val numStatuses = { count: Int -> "$count Estats" }
    override val deleteOrder = "Eliminar Comanda"
    override val deleteOrderConfirm = "Estàs segur que vols eliminar aquesta comanda?"
    override val delete = "Eliminar"
    override val waiter = "Cambrer"
    override val notes = "Notes"
    override val noOrders = "No hi ha comandes"
    override val statusPending = "Pendent"
    override val statusPreparing = "Preparant"
    override val statusReady = "Llest"
    override val statusSent = "Enviada"
    override val statusServed = "Servida"
    override val statusCancelled = "Cancel·lada"
    override val changePassword = "Canviar Contrasenya"
    override val newUser = "Nou Usuari"
    override val notifications = "Notificacions"
    override val notificationsDesc = "Rebre notificacions de noves comandes"
    override val sound = "So"
    override val soundDesc = "Reproduir so quan arriba una comanda"
    override val darkMode = "Mode fosc"
    override val darkModeDesc = "Canviar a tema fosc"
    override val autoPrint = "Impressió automàtica"
    override val autoPrintDesc = "Imprimir comandes automàticament"
    override val appInfo = "Informació de l'App"
    override val version = "Versió"
    override val lastUpdate = "Última actualització"
    override val developedBy = "Desenvolupat per"
    override val syncData = "Sincronitzar Dades"
    override val clearCache = "Esborrar Caché"
    override val firstName = "Nom"
    override val lastName = "Cognoms"
    override val email = "Email"
    override val userRole = "Rol de l'usuari"
    override val roleWaiter = "Cambrer"
    override val roleAdmin = "Administrador"
    override val register = "Registrar"
    override val language = "Idioma"
    override val editModeActive = "Mode edició activat"
    override val addTable = "Afegir Taula"
    override val deleteTable = "Eliminar Taula"
    override val deleteTableConfirm = { n: Int -> "Segur que vols eliminar la taula $n?" }
    override val tableNumber = "Número de taula"
    override val tableCapacity = "Capacitat"
    override val tableAlreadyExists = "Ja existeix una taula amb aquest número"
    override val save = "Desar"
    override val editMode = "Mode Edició"
    override val exitEditMode = "Sortir del mode edició"
    override val cannotDeleteTableWithOrders = "No es pot eliminar una taula que té comandes actives."
    override val componentSize = "Mida dels components"
    override val sizeSmall = "Petit"
    override val sizeMedium = "Mitjà"
    override val sizeLarge = "Gran"
    override val articles = "Articles"
    override val addArticle = "Afegir Article"
    override val editArticle = "Editar Article"
    override val deleteArticle = "Eliminar Article"
    override val articleName = "Nom de l'article"
    override val articlePrice = "Preu"
    override val articleCategory = "Categoria"
    override val articleDescription = "Descripció"
    override val categoryPrimers = "Primers"
    override val categorySegons = "Segons"
    override val categoryPostres = "Postres"
    override val categoryBegudes = "Begudes"
    override val categoryMenus = "Menús"
    override val articleAvailable = "Article disponible"
    override val outOfStock = "Fora d'estoc"
    override val networkConfig = "Configuració de xarxa"
    override val serverUrl = "URL del Servidor"
    override val autoDiscover = "Cercar automàticament"
    override val discovering = "Cercant servidor..."
    override val internalServer = "Mode Host (Servidor Intern)"
    override val internalServerDesc = "Activa el servidor en aquest PC. Desactiva-ho per connectar-te només a un servidor extern (núvol o un altro PC)."
    override val restartRequired = "Cal reiniciar l'aplicació en canviar aquest mode."
    override val selectMode = "Selecciona el mode de funcionament"
    override val modeHost = "Mode Host (Servidor)"
    override val modeHostDesc = "Aquest ordinador actuarà com a servidor principal de dades."
    override val modeClient = "Mode Client"
    override val modeClientDesc = "Connecta't a un servidor remot (núvol u altre ordinador)."
    override val startingServer = "Iniciant el servidor..."
    override val serverStartError = "Error en iniciar el servidor"
}

object SpanishStrings : Strings {
    override val appTitle = "ChefLink"
    override val tables = "Mesas"
    override val orders = "Comandas"
    override val settings = "Configuración"
    override val logout = "Salir"
    override val login = "Iniciar sesión"
    override val username = "Nombre de usuario"
    override val password = "Contraseña"
    override val loginError = "Credenciales inválidas. Inténtalo de nuevo."
    override val connecting = "Conectando con el servidor..."
    override val initializing = "Iniciando ChefLink..."
    override val serverNotFound = "No se ha podido encontrar el servidor"
    override val connectionDetails = { detail: String -> "Detalle: $detail" }
    override val checkServerStatus = "Comprueba que el servidor esté encendido y la red sea correcta."
    override val retry = "Reintentar"
    override val table = "Mesa"
    override val tablesTitle = "Mesas"
    override val allTables = "Todas las mesas"
    override val free = "Libre"
    override val occupied = "Ocupada"
    override val paxs = "paxs"
    override val orderTitle = "Comanda"
    override val orderSummary = "Resumen de la Comanda"
    override val emptyOrder = "La comanda está vacía"
    override val generalNotes = "Notas generales"
    override val total = "Total"
    override val send = "Enviar"
    override val close = "Cerrar"
    override val cancel = "Cancelar"
    override val addNoteTo = "Añadir nota a"
    override val note = "Nota"
    override val addNote = "Añadir nota"
    override val noDescription = "Sin descripción disponible"
    override val filterByTable = "Filtrar por Mesa"
    override val filterByTableDesc = "Selecciona una mesa para ver solo sus comandas."
    override val filterByStatus = "Filtrar por Estado"
    override val allStatuses = "Todos los estados"
    override val numStatuses = { count: Int -> "$count Estados" }
    override val deleteOrder = "Eliminar Comanda"
    override val deleteOrderConfirm = "¿Estás seguro de que quieres eliminar esta comanda?"
    override val delete = "Eliminar"
    override val waiter = "Camarero"
    override val notes = "Notas"
    override val noOrders = "No hay comandas"
    override val statusPending = "Pendiente"
    override val statusPreparing = "Preparando"
    override val statusReady = "Listo"
    override val statusSent = "Enviada"
    override val statusServed = "Servida"
    override val statusCancelled = "Cancelada"
    override val changePassword = "Cambiar Contraseña"
    override val newUser = "Nuevo Usuario"
    override val notifications = "Notificaciones"
    override val notificationsDesc = "Recibir notificaciones de nuevas comandas"
    override val sound = "Sonido"
    override val soundDesc = "Reproducir sonido cuando llega una comanda"
    override val darkMode = "Modo oscuro"
    override val darkModeDesc = "Cambiar a tema oscuro"
    override val autoPrint = "Impresión automática"
    override val autoPrintDesc = "Imprimir comandas automáticamente"
    override val appInfo = "Información de la App"
    override val version = "Versión"
    override val lastUpdate = "Última actualización"
    override val developedBy = "Desarrollado por"
    override val syncData = "Sincronizar Datos"
    override val clearCache = "Borrar Caché"
    override val firstName = "Nombre"
    override val lastName = "Apellidos"
    override val email = "Email"
    override val userRole = "Rol del usuario"
    override val roleWaiter = "Camarero"
    override val roleAdmin = "Administrador"
    override val register = "Registrar"
    override val language = "Idioma"
    override val editModeActive = "Modo edición activado"
    override val addTable = "Añadir Mesa"
    override val deleteTable = "Eliminar Mesa"
    override val deleteTableConfirm = { n: Int -> "¿Seguro que quieres eliminar la mesa $n?" }
    override val tableNumber = "Número de mesa"
    override val tableCapacity = "Capacidad"
    override val tableAlreadyExists = "Ya existe una mesa con este número"
    override val save = "Guardar"
    override val editMode = "Modo Edición"
    override val exitEditMode = "Salir del modo edición"
    override val cannotDeleteTableWithOrders = "No se puede eliminar una mesa que tiene comandas activas."
    override val componentSize = "Tamaño de componentes"
    override val sizeSmall = "Pequeño"
    override val sizeMedium = "Mediano"
    override val sizeLarge = "Grande"
    override val articles = "Artículos"
    override val addArticle = "Añadir Artículo"
    override val editArticle = "Editar Artículo"
    override val deleteArticle = "Eliminar Artículo"
    override val articleName = "Nombre del artículo"
    override val articlePrice = "Precio"
    override val articleCategory = "Categoría"
    override val articleDescription = "Descripción"
    override val categoryPrimers = "Primeros"
    override val categorySegons = "Segundos"
    override val categoryPostres = "Postres"
    override val categoryBegudes = "Bebidas"
    override val categoryMenus = "Menús"
    override val articleAvailable = "Artículo disponible"
    override val outOfStock = "Sin stock"
    override val networkConfig = "Configuración de red"
    override val serverUrl = "URL del Servidor"
    override val autoDiscover = "Buscar automáticamente"
    override val discovering = "Buscando servidor..."
    override val internalServer = "Modo Host (Servidor Intern)"
    override val internalServerDesc = "Activa el servidor en este PC. Desactívalo para conectarte solo a un servidor externo (nube u otro PC)."
    override val restartRequired = "Es necesario reiniciar la aplicación al cambiar este modo."
    override val selectMode = "Selecciona el modo de funcionamiento"
    override val modeHost = "Modo Host (Servidor)"
    override val modeHostDesc = "Este ordenador actuará como servidor principal de datos."
    override val modeClient = "Modo Client"
    override val modeClientDesc = "Conéctate a un servidor remoto (nube u otro ordenador)."
    override val startingServer = "Iniciando el servidor..."
    override val serverStartError = "Error al iniciar el servidor"
}

object EnglishStrings : Strings {
    override val appTitle = "ChefLink"
    override val tables = "Tables"
    override val orders = "Orders"
    override val settings = "Settings"
    override val logout = "Logout"
    override val login = "Login"
    override val username = "Username"
    override val password = "Password"
    override val loginError = "Invalid credentials. Please try again."
    override val connecting = "Connecting to server..."
    override val initializing = "Starting ChefLink..."
    override val serverNotFound = "Server not found"
    override val connectionDetails = { detail: String -> "Details: $detail" }
    override val checkServerStatus = "Check that the server is running and the network is correct."
    override val retry = "Retry"
    override val table = "Table"
    override val tablesTitle = "Tables"
    override val allTables = "All tables"
    override val free = "Free"
    override val occupied = "Occupied"
    override val paxs = "paxs"
    override val orderTitle = "Order"
    override val orderSummary = "Order Summary"
    override val emptyOrder = "The order is empty"
    override val generalNotes = "General notes"
    override val total = "Total"
    override val send = "Send"
    override val close = "Close"
    override val cancel = "Cancel"
    override val addNoteTo = "Add note to"
    override val note = "Note"
    override val addNote = "Add note"
    override val noDescription = "No description available"
    override val filterByTable = "Filter by Table"
    override val filterByTableDesc = "Select a table to see only its orders."
    override val filterByStatus = "Filter by Status"
    override val allStatuses = "All statuses"
    override val numStatuses = { count: Int -> "$count Statuses" }
    override val deleteOrder = "Delete Order"
    override val deleteOrderConfirm = "Are you sure you want to delete this order?"
    override val delete = "Delete"
    override val waiter = "Waiter"
    override val notes = "Notes"
    override val noOrders = "No orders found"
    override val statusPending = "Pending"
    override val statusPreparing = "Preparing"
    override val statusReady = "Ready"
    override val statusSent = "Sent"
    override val statusServed = "Served"
    override val statusCancelled = "Cancelled"
    override val changePassword = "Change Password"
    override val newUser = "New User"
    override val notifications = "Notifications"
    override val notificationsDesc = "Receive notifications for new orders"
    override val sound = "Sound"
    override val soundDesc = "Play sound when an order arrives"
    override val darkMode = "Dark mode"
    override val darkModeDesc = "Switch to dark theme"
    override val autoPrint = "Auto print"
    override val autoPrintDesc = "Automatically print orders"
    override val appInfo = "App Info"
    override val version = "Version"
    override val lastUpdate = "Last update"
    override val developedBy = "Developed by"
    override val syncData = "Sync Data"
    override val clearCache = "Clear Cache"
    override val firstName = "First Name"
    override val lastName = "Last Name"
    override val email = "Email"
    override val userRole = "User Role"
    override val roleWaiter = "Waiter"
    override val roleAdmin = "Administrator"
    override val register = "Register"
    override val language = "Language"
    override val editModeActive = "Edit mode active"
    override val addTable = "Add Table"
    override val deleteTable = "Delete Table"
    override val deleteTableConfirm = { n: Int -> "Are you sure you want to delete table $n?" }
    override val tableNumber = "Table number"
    override val tableCapacity = "Capacity"
    override val tableAlreadyExists = "A table with this number already exists"
    override val save = "Save"
    override val editMode = "Edit Mode"
    override val exitEditMode = "Exit edit mode"
    override val cannotDeleteTableWithOrders = "Cannot delete a table that has active orders."
    override val componentSize = "Component Size"
    override val sizeSmall = "Small"
    override val sizeMedium = "Medium"
    override val sizeLarge = "Large"
    override val articles = "Articles"
    override val addArticle = "Add Article"
    override val editArticle = "Edit Article"
    override val deleteArticle = "Delete Article"
    override val articleName = "Article Name"
    override val articlePrice = "Price"
    override val articleCategory = "Category"
    override val articleDescription = "Description"
    override val categoryPrimers = "Starters"
    override val categorySegons = "Main Courses"
    override val categoryPostres = "Desserts"
    override val categoryBegudes = "Drinks"
    override val categoryMenus = "Menus"
    override val articleAvailable = "Article available"
    override val outOfStock = "Out of stock"
    override val networkConfig = "Network Configuration"
    override val serverUrl = "Server URL"
    override val autoDiscover = "Auto-discover"
    override val discovering = "Discovering server..."
    override val internalServer = "Host Mode (Internal Server)"
    override val internalServerDesc = "Enable the server on this PC. Disable it to connect only to an external server (cloud or another PC)."
    override val restartRequired = "A restart is required when changing this mode."
    override val selectMode = "Select Operation Mode"
    override val modeHost = "Host Mode (Server)"
    override val modeHostDesc = "This computer will act as the primary data server."
    override val modeClient = "Client Mode"
    override val modeClientDesc = "Connect to a remote server (cloud or another computer)."
    override val startingServer = "Starting server..."
    override val serverStartError = "Error starting server"
}

object FrenchStrings : Strings {
    override val appTitle = "ChefLink"
    override val tables = "Tables"
    override val orders = "Commandes"
    override val settings = "Paramètres"
    override val logout = "Déconnexion"
    override val login = "Connexion"
    override val username = "Nom d'utilisateur"
    override val password = "Mot de passe"
    override val loginError = "Identifiants invalides. Veuillez réessayer."
    override val connecting = "Connexion au serveur..."
    override val initializing = "Démarrage de ChefLink..."
    override val serverNotFound = "Serveur introuvable"
    override val connectionDetails = { detail: String -> "Détails: $detail" }
    override val checkServerStatus = "Vérifiez que le serveur est allumé et que le réseau est correct."
    override val retry = "Réessayer"
    override val table = "Table"
    override val tablesTitle = "Tables"
    override val allTables = "Toutes les tables"
    override val free = "Libre"
    override val occupied = "Occupée"
    override val paxs = "paxs"
    override val orderTitle = "Commande"
    override val orderSummary = "Résumé de la Commande"
    override val emptyOrder = "La commande est vide"
    override val generalNotes = "Notes générales"
    override val total = "Total"
    override val send = "Envoyer"
    override val close = "Fermer"
    override val cancel = "Annuler"
    override val addNoteTo = "Ajouter une note à"
    override val note = "Note"
    override val addNote = "Ajouter une note"
    override val noDescription = "Aucune description disponible"
    override val filterByTable = "Filtrer par Table"
    override val filterByTableDesc = "Sélectionnez une table pour voir uniquement ses commandes."
    override val filterByStatus = "Filtrer par État"
    override val allStatuses = "Tous les états"
    override val numStatuses = { count: Int -> "$count États" }
    override val deleteOrder = "Supprimer la Commande"
    override val deleteOrderConfirm = "Êtes-vous sûr de vouloir supprimer cette commande ?"
    override val delete = "Supprimer"
    override val waiter = "Serveur"
    override val notes = "Notes"
    override val noOrders = "Aucune commande trouvée"
    override val statusPending = "En attente"
    override val statusPreparing = "En préparation"
    override val statusReady = "Prêt"
    override val statusSent = "Envoyée"
    override val statusServed = "Servie"
    override val statusCancelled = "Annulée"
    override val changePassword = "Changer le mot de passe"
    override val newUser = "Nouvel Utilisateur"
    override val notifications = "Notifications"
    override val notificationsDesc = "Recevoir des notifications pour les nouvelles commandes"
    override val sound = "Son"
    override val soundDesc = "Jouer un son à l'arrivée d'une commande"
    override val darkMode = "Mode sombre"
    override val darkModeDesc = "Passer au thème sombre"
    override val autoPrint = "Impression automatique"
    override val autoPrintDesc = "Imprimer automatiquement les commandes"
    override val appInfo = "Infos de l'App"
    override val version = "Version"
    override val lastUpdate = "Dernière mise à jour"
    override val developedBy = "Développé par"
    override val syncData = "Synchroniser les données"
    override val clearCache = "Effacer le cache"
    override val firstName = "Prénom"
    override val lastName = "Nom de famille"
    override val email = "Email"
    override val userRole = "Rôle de l'utilisateur"
    override val roleWaiter = "Serveur"
    override val roleAdmin = "Administrateur"
    override val register = "S'inscrire"
    override val language = "Langue"
    override val editModeActive = "Mode édition actif"
    override val addTable = "Ajouter une table"
    override val deleteTable = "Supprimer la table"
    override val deleteTableConfirm = { n: Int -> "Voulez-vous vraiment supprimer la table $n ?" }
    override val tableNumber = "Numéro de table"
    override val tableCapacity = "Capacité"
    override val tableAlreadyExists = "Une table avec ce numéro existe déjà"
    override val save = "Enregistrer"
    override val editMode = "Mode Édition"
    override val exitEditMode = "Quitter le mode édition"
    override val cannotDeleteTableWithOrders = "Impossible de supprimer une table avec des commandes actives."
    override val componentSize = "Taille des composants"
    override val sizeSmall = "Petit"
    override val sizeMedium = "Moyen"
    override val sizeLarge = "Grand"
    override val articles = "Articles"
    override val addArticle = "Ajouter un article"
    override val editArticle = "Modifier l'article"
    override val deleteArticle = "Supprimer l'article"
    override val articleName = "Nom de l'article"
    override val articlePrice = "Prix"
    override val articleCategory = "Catégorie"
    override val articleDescription = "Description"
    override val categoryPrimers = "Entrées"
    override val categorySegons = "Plats Principaux"
    override val categoryPostres = "Desserts"
    override val categoryBegudes = "Boissons"
    override val categoryMenus = "Menus"
    override val articleAvailable = "Article disponible"
    override val outOfStock = "Rupture de stock"
    override val networkConfig = "Configuration Réseau"
    override val serverUrl = "URL du Serveur"
    override val autoDiscover = "Recherche automatique"
    override val discovering = "Recherche du serveur..."
    override val internalServer = "Mode Host (Serveur Interne)"
    override val internalServerDesc = "Activez le serveur sur ce PC. Désactivez-le pour vous connecter uniquement à un serveur externe (cloud ou autre PC)."
    override val restartRequired = "L'application doit être redémarrée pour appliquer ce changement."
    override val selectMode = "Sélectionnez le mode de fonctionnement"
    override val modeHost = "Mode Host (Serveur)"
    override val modeHostDesc = "Cet ordinateur servira de serveur de données principal."
    override val modeClient = "Mode Client"
    override val modeClientDesc = "Connectez-vous à un serveur distant (cloud ou autre ordinateur)."
    override val startingServer = "Démarrage du serveur..."
    override val serverStartError = "Erreur lors du démarrage du serveur"
}

val LocalChefLinkStrings = staticCompositionLocalOf<Strings> { CatalanStrings }
