package dev.hbgl.hhn.schattenbuchhaltung.service.parser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LedgerEntryInstantParser {

    @Value("${app.ledger.datetime.pattern}")
    private String pattern;

    @Value("${app.ledger.datetime.timeZone}")
    private String timeZone;

    public Instant parse(String str) {
        var formatter = DateTimeFormatter.ofPattern(this.pattern);
        var zoneId = ZoneId.of(this.timeZone);
        var dateTime = LocalDateTime.parse(str, formatter).atZone(zoneId);
        return dateTime.toInstant();
    }
}
