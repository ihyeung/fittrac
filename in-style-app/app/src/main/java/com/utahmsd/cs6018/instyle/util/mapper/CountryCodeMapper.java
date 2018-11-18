package com.utahmsd.cs6018.instyle.util.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CountryCodeMapper {

    private final static HashMap<String, String> countryCodeMap = new HashMap<>();
    private static ArrayList<String> countryKeys = new ArrayList<>();

    public CountryCodeMapper() {
        countryCodeMap.put("Andorra, Principality Of", "AD");
        countryCodeMap.put("United Arab Emirates", "AE");
        countryCodeMap.put("Afghanistan, Islamic State Of", "AF");
        countryCodeMap.put("Antigua And Barbuda", "AG");
        countryCodeMap.put("Anguilla", "AI");
        countryCodeMap.put("Albania", "AL");
        countryCodeMap.put("Armenia", "AM");
        countryCodeMap.put("Netherlands Antilles", "AN");
        countryCodeMap.put("Angola", "AO");
        countryCodeMap.put("Antarctica", "AQ");
        countryCodeMap.put("Argentina", "AR");
        countryCodeMap.put("American Samoa", "AS");
        countryCodeMap.put("Austria", "AT");
        countryCodeMap.put("Australia", "AU");
        countryCodeMap.put("Aruba", "AW");
        countryCodeMap.put("Azerbaidjan", "AZ");
        countryCodeMap.put("Bosnia-Herzegovina", "BA");
        countryCodeMap.put("Barbados", "BB");
        countryCodeMap.put("Bangladesh", "BD");
        countryCodeMap.put("Belgium", "BE");
        countryCodeMap.put("Burkina Faso", "BF");
        countryCodeMap.put("Bulgaria", "BG");
        countryCodeMap.put("Bahrain", "BH");
        countryCodeMap.put("Burundi", "BI");
        countryCodeMap.put("Benin", "BJ");
        countryCodeMap.put("Bermuda", "BM");
        countryCodeMap.put("Brunei Darussalam", "BN");
        countryCodeMap.put("Bolivia", "BO");
        countryCodeMap.put("Brazil", "BR");
        countryCodeMap.put("Bahamas", "BS");
        countryCodeMap.put("Bhutan", "BT");
        countryCodeMap.put("Bouvet Island", "BV");
        countryCodeMap.put("Botswana", "BW");
        countryCodeMap.put("Belarus", "BY");
        countryCodeMap.put("Belize", "BZ");
        countryCodeMap.put("Canada", "CA");
        countryCodeMap.put("Cocos (Keeling) Islands", "CC");
        countryCodeMap.put("Central African Republic", "CF");
        countryCodeMap.put("Congo, The Democratic Republic Of The", "CD");
        countryCodeMap.put("Congo", "CG");
        countryCodeMap.put("Switzerland", "CH");
        countryCodeMap.put("Ivory Coast (Cote D'Ivoire)", "CI");
        countryCodeMap.put("Cook Islands", "CK");
        countryCodeMap.put("Chile", "CL");
        countryCodeMap.put("Cameroon", "CM");
        countryCodeMap.put("China", "CN");
        countryCodeMap.put("Colombia", "CO");
        countryCodeMap.put("Costa Rica", "CR");
        countryCodeMap.put("Former Czechoslovakia", "CS");
        countryCodeMap.put("Cuba", "CU");
        countryCodeMap.put("Cape Verde", "CV");
        countryCodeMap.put("Christmas Island", "CX");
        countryCodeMap.put("Cyprus", "CY");
        countryCodeMap.put("Czech Republic", "CZ");
        countryCodeMap.put("Germany", "DE");
        countryCodeMap.put("Djibouti", "DJ");
        countryCodeMap.put("Denmark", "DK");
        countryCodeMap.put("Dominica", "DM");
        countryCodeMap.put("Dominican Republic", "DO");
        countryCodeMap.put("Algeria", "DZ");
        countryCodeMap.put("Ecuador", "EC");
        countryCodeMap.put("Estonia", "EE");
        countryCodeMap.put("Egypt", "EG");
        countryCodeMap.put("Western Sahara", "EH");
        countryCodeMap.put("Eritrea", "ER");
        countryCodeMap.put("Spain", "ES");
        countryCodeMap.put("Ethiopia", "ET");
        countryCodeMap.put("Finland", "FI");
        countryCodeMap.put("Fiji", "FJ");
        countryCodeMap.put("Falkland Islands", "FK");
        countryCodeMap.put("Micronesia", "FM");
        countryCodeMap.put("Faroe Islands", "FO");
        countryCodeMap.put("France", "FR");
        countryCodeMap.put("France (European Territory)", "FX");
        countryCodeMap.put("Gabon", "GA");
        countryCodeMap.put("Great Britain", "UK");
        countryCodeMap.put("Grenada", "GD");
        countryCodeMap.put("Georgia", "GE");
        countryCodeMap.put("French Guyana", "GF");
        countryCodeMap.put("Ghana", "GH");
        countryCodeMap.put("Gibraltar", "GI");
        countryCodeMap.put("Greenland", "GL");
        countryCodeMap.put("Gambia", "GM");
        countryCodeMap.put("Guinea", "GN");
        countryCodeMap.put("Guadeloupe (French)", "GP");
        countryCodeMap.put("Equatorial Guinea", "GQ");
        countryCodeMap.put("Greece", "GR");
        countryCodeMap.put("S. Georgia & S. Sandwich Isls.", "GS");
        countryCodeMap.put("Guatemala", "GT");
        countryCodeMap.put("Guam (USA)", "GU");
        countryCodeMap.put("Guinea Bissau", "GW");
        countryCodeMap.put("Guyana", "GY");
        countryCodeMap.put("Hong Kong", "HK");
        countryCodeMap.put("Heard And McDonald Islands", "HM");
        countryCodeMap.put("Honduras", "HN");
        countryCodeMap.put("Croatia", "HR");
        countryCodeMap.put("Haiti", "HT");
        countryCodeMap.put("Hungary", "HU");
        countryCodeMap.put("Indonesia", "ID");
        countryCodeMap.put("Ireland", "IE");
        countryCodeMap.put("Israel", "IL");
        countryCodeMap.put("India", "IN");
        countryCodeMap.put("British Indian Ocean Territory", "IO");
        countryCodeMap.put("Iraq", "IQ");
        countryCodeMap.put("Iran", "IR");
        countryCodeMap.put("Iceland", "IS");
        countryCodeMap.put("Italy", "IT");
        countryCodeMap.put("Jamaica", "JM");
        countryCodeMap.put("Jordan", "JO");
        countryCodeMap.put("Japan", "JP");
        countryCodeMap.put("Kenya", "KE");
        countryCodeMap.put("Kyrgyz Republic (Kyrgyzstan)", "KG");
        countryCodeMap.put("Cambodia, Kingdom Of", "KH");
        countryCodeMap.put("Kiribati", "KI");
        countryCodeMap.put("Comoros", "KM");
        countryCodeMap.put("Saint Kitts & Nevis Anguilla", "KN");
        countryCodeMap.put("Korea", "KR");
        countryCodeMap.put("Kuwait", "KW");
        countryCodeMap.put("Cayman Islands", "KY");
        countryCodeMap.put("Kazakhstan", "KZ");
        countryCodeMap.put("Laos", "LA");
        countryCodeMap.put("Lebanon", "LB");
        countryCodeMap.put("Saint Lucia", "LC");
        countryCodeMap.put("Liechtenstein", "LI");
        countryCodeMap.put("Sri Lanka", "LK");
        countryCodeMap.put("Liberia", "LR");
        countryCodeMap.put("Lesotho", "LS");
        countryCodeMap.put("Lithuania", "LT");
        countryCodeMap.put("Luxembourg", "LU");
        countryCodeMap.put("Latvia", "LV");
        countryCodeMap.put("Libya", "LY");
        countryCodeMap.put("Morocco", "MA");
        countryCodeMap.put("Monaco", "MC");
        countryCodeMap.put("Moldavia", "MD");
        countryCodeMap.put("Madagascar", "MG");
        countryCodeMap.put("Marshall Islands", "MH");
        countryCodeMap.put("Macedonia", "MK");
        countryCodeMap.put("Mali", "ML");
        countryCodeMap.put("Myanmar", "MM");
        countryCodeMap.put("Mongolia", "MN");
        countryCodeMap.put("Macau", "MO");
        countryCodeMap.put("Northern Mariana Islands", "MP");
        countryCodeMap.put("Martinique (French)", "MQ");
        countryCodeMap.put("Mauritania", "MR");
        countryCodeMap.put("Montserrat", "MS");
        countryCodeMap.put("Malta", "MT");
        countryCodeMap.put("Mauritius", "MU");
        countryCodeMap.put("Maldives", "MV");
        countryCodeMap.put("Malawi", "MW");
        countryCodeMap.put("Mexico", "MX");
        countryCodeMap.put("Malaysia", "MY");
        countryCodeMap.put("Mozambique", "MZ");
        countryCodeMap.put("Namibia", "NA");
        countryCodeMap.put("New Caledonia (French)", "NC");
        countryCodeMap.put("Niger", "NE");
        countryCodeMap.put("Norfolk Island", "NF");
        countryCodeMap.put("Nigeria", "NG");
        countryCodeMap.put("Nicaragua", "NI");
        countryCodeMap.put("Netherlands", "NL");
        countryCodeMap.put("Norway", "NO");
        countryCodeMap.put("Nepal", "NP");
        countryCodeMap.put("Nauru", "NR");
        countryCodeMap.put("Neutral Zone", "NT");
        countryCodeMap.put("Niue", "NU");
        countryCodeMap.put("New Zealand", "NZ");
        countryCodeMap.put("Oman", "OM");
        countryCodeMap.put("Panama", "PA");
        countryCodeMap.put("Peru", "PE");
        countryCodeMap.put("Polynesia (French)", "PF");
        countryCodeMap.put("Papua New Guinea", "PG");
        countryCodeMap.put("Philippines", "PH");
        countryCodeMap.put("Pakistan", "PK");
        countryCodeMap.put("Poland", "PL");
        countryCodeMap.put("Saint Pierre And Miquelon", "PM");
        countryCodeMap.put("Pitcairn Island", "PN");
        countryCodeMap.put("Puerto Rico", "PR");
        countryCodeMap.put("Portugal", "PT");
        countryCodeMap.put("Palau", "PW");
        countryCodeMap.put("Paraguay", "PY");
        countryCodeMap.put("Qatar", "QA");
        countryCodeMap.put("Reunion (French)", "RE");
        countryCodeMap.put("Romania", "RO");
        countryCodeMap.put("Russian Federation", "RU");
        countryCodeMap.put("Rwanda", "RW");
        countryCodeMap.put("Saudi Arabia", "SA");
        countryCodeMap.put("Solomon Islands", "SB");
        countryCodeMap.put("Seychelles", "SC");
        countryCodeMap.put("Sudan", "SD");
        countryCodeMap.put("Sweden", "SE");
        countryCodeMap.put("Singapore", "SG");
        countryCodeMap.put("Saint Helena", "SH");
        countryCodeMap.put("Slovenia", "SI");
        countryCodeMap.put("Svalbard And Jan Mayen Islands", "SJ");
        countryCodeMap.put("Slovak Republic", "SK");
        countryCodeMap.put("Sierra Leone", "SL");
        countryCodeMap.put("San Marino", "SM");
        countryCodeMap.put("Senegal", "SN");
        countryCodeMap.put("Somalia", "SO");
        countryCodeMap.put("Suriname", "SR");
        countryCodeMap.put("Saint Tome (Sao Tome) And Principe", "ST");
        countryCodeMap.put("Former USSR", "SU");
        countryCodeMap.put("El Salvador", "SV");
        countryCodeMap.put("Syria", "SY");
        countryCodeMap.put("Swaziland", "SZ");
        countryCodeMap.put("Turks And Caicos Islands", "TC");
        countryCodeMap.put("Chad", "TD");
        countryCodeMap.put("French Southern Territories", "TF");
        countryCodeMap.put("Togo", "TG");
        countryCodeMap.put("Thailand", "TH");
        countryCodeMap.put("Tadjikistan", "TJ");
        countryCodeMap.put("Tokelau", "TK");
        countryCodeMap.put("Turkmenistan", "TM");
        countryCodeMap.put("Tunisia", "TN");
        countryCodeMap.put("Tonga", "TO");
        countryCodeMap.put("East Timor", "TP");
        countryCodeMap.put("Turkey", "TR");
        countryCodeMap.put("Trinidad And Tobago", "TT");
        countryCodeMap.put("Tuvalu", "TV");
        countryCodeMap.put("Taiwan", "TW");
        countryCodeMap.put("Tanzania", "TZ");
        countryCodeMap.put("Ukraine", "UA");
        countryCodeMap.put("Uganda", "UG");
        countryCodeMap.put("United Kingdom", "UK");
        countryCodeMap.put("USA Minor Outlying Islands", "UM");
        countryCodeMap.put("United States", "US");
        countryCodeMap.put("Uruguay", "UY");
        countryCodeMap.put("Uzbekistan", "UZ");
        countryCodeMap.put("Holy See (Vatican City State)", "VA");
        countryCodeMap.put("Saint Vincent & Grenadines", "VC");
        countryCodeMap.put("Venezuela", "VE");
        countryCodeMap.put("Virgin Islands (British)", "VG");
        countryCodeMap.put("Virgin Islands (USA)", "VI");
        countryCodeMap.put("Vietnam", "VN");
        countryCodeMap.put("Vanuatu", "VU");
        countryCodeMap.put("Wallis And Futuna Islands", "WF");
        countryCodeMap.put("Samoa", "WS");
        countryCodeMap.put("Yemen", "YE");
        countryCodeMap.put("Mayotte", "YT");
        countryCodeMap.put("Yugoslavia", "YU");
        countryCodeMap.put("South Africa", "ZA");
        countryCodeMap.put("Zambia", "ZM");
        countryCodeMap.put("Zaire", "ZR");
        countryCodeMap.put("Zimbabwe", "ZW");

        countryKeys.addAll(countryCodeMap.keySet());
        Collections.sort(countryKeys);
    }

    public static String getCountryCode(String countryName){
        if (countryCodeMap.isEmpty()) {
            CountryCodeMapper mapper = new CountryCodeMapper();
        }
        return countryCodeMap.getOrDefault(countryName, null);
    }

    public static String getCountryName(String countryCode){
        if (countryCodeMap.isEmpty()) {
            CountryCodeMapper mapper = new CountryCodeMapper();
        }
        if (countryCodeMap.containsValue(countryCode)) {
            for (Map.Entry<String, String> each : countryCodeMap.entrySet()) {
                if (each.getValue().equals(countryCode)) {
                    return each.getKey();
                }
            }
        }
        //Otherwise return default
        return null;
    }

    public static HashMap<String, String> getMapper() {
        if (countryCodeMap.isEmpty()) {
            CountryCodeMapper mapper = new CountryCodeMapper();
        }
        return countryCodeMap;
    }

    public static ArrayList<String> getCountryNames() {
        if (countryKeys.isEmpty()) {
            CountryCodeMapper mapper = new CountryCodeMapper();
        }
        return countryKeys;
    }
}
