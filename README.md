# ⚙️ HashConfig v0.0.1 - Guide d'utilisation

## Description de la librairie
Cette librairie est faîte pour manipuler des fichiers de configuration ainsi que des `.env` plus facilement.

---

## 🏁 Getting Started

### Classe
```java
HashConfig(Class<?> plugin, String resourcePath, String outputPath, boolean withDotEnv);
```

### Paramètres
- `Class<?> plugin`: La classe principale du plugin.
- `String resourcePath`: Le chemin du fichier de configuration se trouvant dans votre `.jar`. *(Appelé ressource)*
- `String outputPath`: Le chemin vers la sauvegarde locale du fichier de configuration.
- `boolean withDotEnv`: S'il faut charger le fichier d'environnement ou non.

> [!warning]
> **Le fichier de configuration ne sera pas chargé/recréé depuis les ressources si il existe déjà en local. Il sera uniquement chargé depuis le fichier local.**


### Utilisation

**Structure du serveur**
```
server/
├─ ...
├─ spigot.jar
├─ plugins/
│  ├─ TonPlugin.jar
├─ ...
```

**Structure de `TonPlugin.jar`**
```
TonPlugin.jar/
├─ config.yml
├─ main/
│  ├─ java/
│  │  ├─ .../
```

**Fichier de configuration: `config.yml`:**
```yaml
users:
    1:
        username: L1x
        password: 1234
    2:
        username: Epitoch
        password: 5678
```

**Fichier de variable d'environnement: `.env`:**
```env
TOKEN=YOUR_TOKEN
```

---

### Codes d'exemple

**Chargement basic du fichier de configuration**
```java
import fr.hashtek.hashconfig.HashConfig;

// Pour un plugin minecraft
public class TonPlugin extends JavaPlugin
{
    
    @Override
    public void onEnable()
    {
        HashConfig config = new HashConfig(
            this.getClass(),
            "config.yml",
            this.getDataFolder().getPath() + "/" + "config.yml",
            false // Définir à true pour charger les fichiers .env
        );
    }

    @Override
    public void onDisable() {}
    
}

// Pour un autre type de projet Java
public class TonProjet
{
    
    public static void main(String[] args)
    {
        HashConfig config = new HashConfig(
            this.getClass(),
            "config.yml",
            "chemin/de/destination/config.yml",
            false // Définir à true pour charger les fichiers .env
        );
    }
    
}
```

**Récupération des variables d'environnement**

```java
import fr.hashtek.hashconfig.HashConfig;

HashConfig config = ...;
String value = config.getEnv().get("key");
```

**Récupération d'un élément de votre fichier de configuration**
```java
HashConfig config = ...;
YamlFile yaml = config.getYaml();

String str = yaml.getString("path.to.your.string");
int number = yaml.getInt("path.to.your.number");
double number2 = yaml.getDouble("path.to.your.double");
// etc...
```

**Modification des valeurs dans le fichier de configuration**
```java
HashConfig config = ...;
YamlFile yaml = config.getYaml();

String your_string = "blabla";
int your_integer = 1234;
double your_double = 1234.2934;

// Définir les nouvelles valeurs.
yaml.set("path.to.your.string", your_string);
yaml.set("path.to.your.integer", your_integer);
yaml.set("path.to.your.double", your_double);

config.save(); // Sauvegarder les nouvelles valeurs.
```

**Rechargement du fichier de configuration**
```java
HashConfig config = ...;

// Vos modifications ici...

config.reload();
```

---

> [!warning]
> ⚠️ RAPPEL DES WARNINGS
> 
> Le fichier de configuration ne sera pas chargé/recréé depuis les ressources s'il existe déjà en local.
> Il sera uniquement chargé depuis le fichier local et non depuis l'archive.
