# EndeMonat - Expense and Budget Management System

## Projektinformationen
- **Team**: Emilio und Leander
- **Zweck**: Verwaltung von Ausgaben und Budget eines Nutzers
- **Technologie-Stack**: Java Spring Boot, MongoDB, VS Code
- **Test-Modul**: Modul 450 (Tests werden später implementiert)

## Entitäten
- **Transaction**: Für die Verwaltung von Finanztransaktionen
- **Category**: Für die Kategorisierung von Ausgaben
- **Budget**: Für die Budgetverwaltung

## Voraussetzungen
- Java 17 oder höher
- Maven 3.6 oder höher
- MongoDB (lokal oder über Docker)

## Installation und Setup

### 1. MongoDB Setup
Stellen Sie sicher, dass MongoDB läuft. Sie können MongoDB lokal installieren oder mit Docker verwenden:

```bash
# Mit Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### 2. Projekt bauen
```bash
mvn clean install
```

### 3. Anwendung starten
```bash
mvn spring-boot:run
```

Die Anwendung läuft dann auf `http://localhost:8080`

## API-Endpunkte

### 🧠 **Intelligente Analyse (NEU!)**
- `GET /api/analysis/end-of-month` - 🎯 **Ende Monat Analyse** - Komplexe Vorhersage der finanziellen Situation
- `GET /api/analysis/survival-prediction` - 🔮 **Überlebensprognose** - Wird das Geld bis Monatsende reichen?
- `GET /api/analysis/category-intelligence` - 🤖 **KI-Kategorie-Analyse** - Intelligente Ausgabenmuster-Erkennung
- `GET /api/analysis/spending-recommendations` - 💡 **Smart Spending Tips** - Personalisierte Ausgaben-Empfehlungen

### 💳 **Smart Transactions**
- `POST /api/transactions/smart` - 🚨 **Intelligente Transaktion** - Mit KI-Validierung und Warnungen
- `GET /api/transactions` - Alle Transaktionen abrufen
- `POST /api/transactions` - Normale Transaktion erstellen
- `GET /api/transactions/{id}` - Transaktion nach ID abrufen
- `PUT /api/transactions/{id}` - Transaktion aktualisieren
- `DELETE /api/transactions/{id}` - Transaktion löschen
- `GET /api/transactions/category/{categoryId}` - Transaktionen nach Kategorie
- `GET /api/transactions/type/{type}` - Transaktionen nach Typ (EXPENSE/INCOME)
- `GET /api/transactions/statistics` - Transaktionsstatistiken

### Categories
- `GET /api/categories` - Alle Kategorien abrufen
- `POST /api/categories` - Neue Kategorie erstellen
- `GET /api/categories/{id}` - Kategorie nach ID abrufen
- `PUT /api/categories/{id}` - Kategorie aktualisieren
- `DELETE /api/categories/{id}` - Kategorie löschen
- `GET /api/categories/active` - Nur aktive Kategorien abrufen
- `PUT /api/categories/{id}/activate` - Kategorie aktivieren
- `PUT /api/categories/{id}/deactivate` - Kategorie deaktivieren

### Budgets
- `GET /api/budgets` - Alle Budgets abrufen
- `POST /api/budgets` - Neues Budget erstellen
- `GET /api/budgets/{id}` - Budget nach ID abrufen
- `PUT /api/budgets/{id}` - Budget aktualisieren
- `DELETE /api/budgets/{id}` - Budget löschen
- `GET /api/budgets/active` - Nur aktive Budgets abrufen
- `GET /api/budgets/current` - Aktuell gültige Budgets abrufen
- `GET /api/budgets/over-budget` - Überzogene Budgets abrufen
- `GET /api/budgets/{id}/progress` - Budget-Fortschritt abrufen

## 🎯 **Neue Intelligente Features**

### 🧠 **"Ende Monat" KI-Analyse**
Das System analysiert automatisch:
- **Überlebenswahrscheinlichkeit** bis Monatsende
- **Kritische Kategorien** mit Ausgabenwarnung  
- **Intelligente Tagesbudgets** basierend auf verbleibendem Geld
- **Ausgabengeschwindigkeit** und Risikobewertung

### 🚨 **Smart Transaction Validation**
Jede Ausgabe wird analysiert mit:
- **Budget-Überschreitung Warnung**
- **Ende-Monat Risiko-Assessment**
- **Wochenend-Ausgaben Kontrolle** 
- **Große Ausgaben Validierung**

### 📊 **Beispiel Smart Transaction Response**
```json
{
  "transaction": { ... },
  "status": "LOW_BUDGET",
  "message": "⚠️ Vorsicht: Nur noch 45 CHF im Budget übrig!"
}
```

### 🔮 **Survival Prediction Examples**
- `GAME_OVER` - 💸 "Game Over! Alle Budgets aufgebraucht!"
- `CRITICAL` - 🆘 "Kritisch! 5 von 7 Kategorien in der Krise!"
- `COMFORTABLE` - 😎 "Entspannt! Du hast sogar noch Puffer!"

## Konfiguration

### Profile
- `dev` - Entwicklungsumgebung (Standard)
- `test` - Testumgebung
- `prod` - Produktionsumgebung

### Datenbankverbindung
Die MongoDB-Verbindung kann in den `application-{profile}.properties` Dateien konfiguriert werden.

## Entwicklungsrichtlinien
- Verwenden Sie Java Spring Boot mit MongoDB-Integration
- Folgen Sie REST API-Design-Prinzipien
- Implementieren Sie die richtige Schichtenarchitektur (Controller -> Service -> Repository)
- Verwenden Sie Spring Data MongoDB für den Datenzugriff
- Halten Sie den Code sauber und gut dokumentiert
- Folgen Sie den Java-Namenskonventionen

## Health Check
Die Anwendung stellt Health-Check-Endpunkte über Spring Boot Actuator bereit:
- `GET /actuator/health` - Gesundheitsstatus der Anwendung
- `GET /actuator/info` - Anwendungsinformationen

## Nächste Schritte
- Implementierung der Tests (Modul 450)
- Frontend-Integration
- Authentifizierung und Autorisierung
- Erweiterte Budgetregeln und Benachrichtigungen