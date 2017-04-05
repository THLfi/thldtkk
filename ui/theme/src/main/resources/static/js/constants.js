"use strict";

angular.module("vtrConstants", [])
        .value("applicationTitle", "Tutkimus- ja ajanvarauspalvelu")
        .value("defaultLanguage", "fi")
        .constant("THEME_COMMON", new ThemeCommon())
        .constant("EVENT_TYPES", [
            "Vapaa",
            "Lukittu",
            "Varattu",
            "Peruttu",
            "Vahvistettu"])
        .constant("PROTOCOL_EVENT_TYPES", {
            QUESTIONNAIRE: "Lomake"
        })
        .constant("STATUS_VALUES", {
            UNDEFINED: 0,
            DONE: 1
        })
        .constant("LANGUAGES", ["fi", "sv", "en"]);
