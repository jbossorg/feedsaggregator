package org.jboss.planet.feeds2mongo;

public class StringTools {

    public static String title2Code(String title) {
        if (title == null) {
            return null;
        }

        char[] titleWithUnderscores = title.toLowerCase().replaceAll("[^a-z0-9_]", "_").toCharArray();

        StringBuffer newTitle = new StringBuffer();

        // Removing _ from the beginning.
        int titleIndex = 0;
        while ((titleIndex < titleWithUnderscores.length) && (titleWithUnderscores[titleIndex] == '_')) {
            titleIndex++;
        }

        // Removing multiple _ in the text.
        boolean previousLetter = true;
        while (titleIndex < titleWithUnderscores.length) {
            if (titleWithUnderscores[titleIndex] == '_') {
                if (previousLetter) {
                    newTitle.append(titleWithUnderscores[titleIndex]);
                }

                previousLetter = false;
            } else {
                newTitle.append(titleWithUnderscores[titleIndex]);
                previousLetter = true;
            }

            titleIndex++;
        }

        // Removing _ from the end, if there was one.
        if ((newTitle.length() > 0) && (newTitle.charAt(newTitle.length() - 1) == '_')) {
            newTitle.deleteCharAt(newTitle.length() - 1);
        }

        return newTitle.toString();
    }

}
