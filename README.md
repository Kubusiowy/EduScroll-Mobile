# EduScroll — Mobilna aplikacja edukacyjna

EduScroll to aplikacja stworzona jako świadoma alternatywa dla zjawiska doomscrollingu. Zamiast bezrefleksyjnego przewijania krótkich, rozrywkowych materiałów, użytkownik otrzymuje krótkie, atrakcyjne treści edukacyjne dotyczące technologii, takich jak cyberbezpieczeństwo, weryfikacja informacji, fake newsy oraz zdrowe nawyki cyfrowe.

Aplikacja wykorzystuje formę dynamicznego feedu, znanego z popularnych social mediów, lecz skupionego na realnej wartości edukacyjnej zamiast bezproduktywnej rozrywki.

---

## Misja projektu

Celem EduScroll jest zmiana nawyków cyfrowych młodych użytkowników poprzez zastąpienie nieproduktywnego scrollowania wartościową, krótką edukacją. Projekt zachęca do świadomego korzystania z technologii i popularyzuje wiedzę z obszaru bezpieczeństwa cyfrowego oraz krytycznego myślenia.

---

## Sposób działania

Aplikacja pobiera materiały edukacyjne z dedykowanego backendu i dynamicznie wyświetla je użytkownikowi w formie przewijanego strumienia treści. Zawartość może być zdalnie aktualizowana i rozwijana, co umożliwia stałe poszerzanie bazy wiedzy w aplikacji.

---

## Technologia

| Warstwa | Wykorzystana technologia |
|---------|--------------------------|
| Aplikacja mobilna | Kotlin, Jetpack Compose |
| Architektura | MVVM, Repository |
| Komunikacja z API | Retrofit, Kotlin Serialization (JSON) |
| Backend | Ktor, MySQL, Exposed (Kotlin) |
| Hosting | Prywatny serwer z domeną eduscroll.pl |

---

## Backend projektu

Repozytorium:  
https://github.com/Kubusiowy/Server-HackHeros

Działający endpoint testowy:  
https://eduscroll.pl/api/ping

---

## Funkcjonalności

- przewijany feed edukacyjny oparty na krótkich treściach
- dynamiczne aktualizacje danych z backendu
- logowanie użytkownika z wykorzystaniem JWT i ról
- rozbudowany system kategorii i materiałów edukacyjnych
- planowane systemy rozwoju użytkownika

---

## Wymagania developerskie

| Wymaganie | Minimalna wersja |
|-----------|------------------|
| Android Studio | Flamingo lub nowsza |
| Język | Kotlin |
| Android | co najmniej 8.0 (API 26) |
| System budowania | Gradle (Kotlin DSL) |

---

## Status projektu

Aplikacja jest w fazie aktywnego rozwoju. Projekt powstaje zarówno jako inicjatywa edukacyjna, jak i realne narzędzie promujące świadome korzystanie z technologii.

---

## Planowany rozwój

- elementy grywalizacji (punkty, rangi, statystyki)
- quizy oraz materiały bazujące na prawdziwych fake newsach i manipulacjach
- rozbudowa panelu backendowego do edycji treści

---
