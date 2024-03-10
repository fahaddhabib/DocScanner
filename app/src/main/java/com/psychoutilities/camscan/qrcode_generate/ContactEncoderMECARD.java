package com.psychoutilities.camscan.qrcode_generate;

import java.util.List;
import java.util.regex.Pattern;

final class ContactEncoderMECARD extends EncoderContact {

    private static final char TERMINATOR = ';';

    @Override
    public String[] encode(List<String> names,
                           String organization,
                           List<String> addresses,
                           List<String> phones,
                           List<String> phoneTypes,
                           List<String> emails,
                           List<String> urls,
                           String note) {
        StringBuilder newContents = new StringBuilder(100);
        newContents.append("MECARD:");

        StringBuilder newDisplayContents = new StringBuilder(100);

        Formatter fieldFormatter = new FieldFormatterMECARD();

        appendUpToUnique(newContents, newDisplayContents, "N", names, 1, new
                NameDisplayFormatterMECARD(), fieldFormatter, TERMINATOR);

        append(newContents, newDisplayContents, "ORG", organization, fieldFormatter, TERMINATOR);

        appendUpToUnique(newContents, newDisplayContents, "ADR", addresses, 1, null, fieldFormatter, TERMINATOR);

        appendUpToUnique(newContents, newDisplayContents, "TEL", phones, Integer.MAX_VALUE,
                new TelDisplayFormatterMECARD(), fieldFormatter, TERMINATOR);

        appendUpToUnique(newContents, newDisplayContents, "EMAIL", emails, Integer.MAX_VALUE, null,
                fieldFormatter, TERMINATOR);

        appendUpToUnique(newContents, newDisplayContents, "URL", urls, Integer.MAX_VALUE, null,
                fieldFormatter, TERMINATOR);

        append(newContents, newDisplayContents, "NOTE", note, fieldFormatter, TERMINATOR);

        newContents.append(';');

        return new String[] { newContents.toString(), newDisplayContents.toString() };
    }

    private static final class TelDisplayFormatterMECARD implements Formatter {
        private static final Pattern NOT_DIGITS_OR_PLUS = Pattern.compile("[^0-9+]+");
        @Override
        public CharSequence format(CharSequence value, int index) {
            return NOT_DIGITS_OR_PLUS.matcher(EncoderContact.formatPhone(value.toString())).replaceAll("");
        }
    }

    private static final class NameDisplayFormatterMECARD implements Formatter {
        private static final Pattern COMMA = Pattern.compile(",");
        @Override
        public CharSequence format(CharSequence value, int index) {
            return COMMA.matcher(value).replaceAll("");
        }
    }

    private static final class FieldFormatterMECARD implements Formatter {
        private static final Pattern RESERVED_MECARD_CHARS = Pattern.compile("([\\\\:;])");
        private static final Pattern NEWLINE = Pattern.compile("\\n");
        @Override
        public CharSequence format(CharSequence value, int index) {
            return ':' + NEWLINE.matcher(RESERVED_MECARD_CHARS.matcher(value).replaceAll("\\\\$1")).replaceAll("");
        }
    }

}
