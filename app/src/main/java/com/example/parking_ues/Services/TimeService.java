package com.example.parking_ues.Services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeService {

    // Formato ISO 8601 compatible con Firebase
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Obtiene la fecha actual en formato ISO 8601 (UTC)
     * @return String con la fecha en formato ISO 8601
     */
    public static String getCurrentTimeISO8601() {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /**
     * Obtiene el timestamp actual en milisegundos desde la época (1/1/1970)
     * @return long con el timestamp en milisegundos
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Obtiene la fecha actual en formato legible (opcional)
     * @return String con la fecha formateada
     */
    public static String getCurrentReadableDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}