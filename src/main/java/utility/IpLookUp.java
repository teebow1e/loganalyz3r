package utility;

import com.maxmind.db.MaxMindDbConstructor;
import com.maxmind.db.MaxMindDbParameter;
import com.maxmind.db.Reader;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class IpLookUp {
    public static String checkIP(String host) throws IOException {
        // CONSTANT VALUE HERE
        File database = new File("GeoLite2-Country.mmdb");
        try (Reader reader = new Reader(database)) {
            InetAddress address = InetAddress.getByName(host);
            LookupResult result = reader.get(address, LookupResult.class);

            if (result != null && result.getCountry() != null) {
                return convertToCountryCode(result.getCountry().getIsoCode());
            } else {
                return "None";
            }
        }
    }

    public static class LookupResult {
        private final Country country;

        @MaxMindDbConstructor
        public LookupResult (@MaxMindDbParameter(name="country") Country country) {
            this.country = country;
        }

        public Country getCountry() {
            return this.country;
        }
    }

    public static class Country {
        private final String isoCode;

        @MaxMindDbConstructor
        public Country(@MaxMindDbParameter(name = "iso_code") String isoCode) {
            this.isoCode = isoCode;
        }

        public String getIsoCode() {
            return this.isoCode;
        }
    }

    public static String convertToCountryCode(String countryCode) {
        return COUNTRY_CODES_MAP.getOrDefault(countryCode, "None");
    }
    private static final Map<String, String> COUNTRY_CODES_MAP = new HashMap<>();
    static {
        COUNTRY_CODES_MAP.put("AF", "Afghanistan");
        COUNTRY_CODES_MAP.put("AL", "Albania");
        COUNTRY_CODES_MAP.put("DZ", "Algeria");
        COUNTRY_CODES_MAP.put("AD", "Andorra");
        COUNTRY_CODES_MAP.put("AO", "Angola");
        COUNTRY_CODES_MAP.put("AG", "Antigua and Barbuda");
        COUNTRY_CODES_MAP.put("AR", "Argentina");
        COUNTRY_CODES_MAP.put("AM", "Armenia");
        COUNTRY_CODES_MAP.put("AU", "Australia");
        COUNTRY_CODES_MAP.put("AT", "Austria");
        COUNTRY_CODES_MAP.put("AZ", "Azerbaijan");
        COUNTRY_CODES_MAP.put("BS", "Bahamas");
        COUNTRY_CODES_MAP.put("BH", "Bahrain");
        COUNTRY_CODES_MAP.put("BD", "Bangladesh");
        COUNTRY_CODES_MAP.put("BB", "Barbados");
        COUNTRY_CODES_MAP.put("BY", "Belarus");
        COUNTRY_CODES_MAP.put("BE", "Belgium");
        COUNTRY_CODES_MAP.put("BZ", "Belize");
        COUNTRY_CODES_MAP.put("BJ", "Benin");
        COUNTRY_CODES_MAP.put("BT", "Bhutan");
        COUNTRY_CODES_MAP.put("BO", "Bolivia");
        COUNTRY_CODES_MAP.put("BA", "Bosnia and Herzegovina");
        COUNTRY_CODES_MAP.put("BW", "Botswana");
        COUNTRY_CODES_MAP.put("BR", "Brazil");
        COUNTRY_CODES_MAP.put("BN", "Brunei");
        COUNTRY_CODES_MAP.put("BG", "Bulgaria");
        COUNTRY_CODES_MAP.put("BF", "Burkina Faso");
        COUNTRY_CODES_MAP.put("BI", "Burundi");
        COUNTRY_CODES_MAP.put("CV", "Cabo Verde");
        COUNTRY_CODES_MAP.put("KH", "Cambodia");
        COUNTRY_CODES_MAP.put("CM", "Cameroon");
        COUNTRY_CODES_MAP.put("CA", "Canada");
        COUNTRY_CODES_MAP.put("CF", "Central African Republic");
        COUNTRY_CODES_MAP.put("TD", "Chad");
        COUNTRY_CODES_MAP.put("CL", "Chile");
        COUNTRY_CODES_MAP.put("CN", "China");
        COUNTRY_CODES_MAP.put("CO", "Colombia");
        COUNTRY_CODES_MAP.put("KM", "Comoros");
        COUNTRY_CODES_MAP.put("CG", "Congo");
        COUNTRY_CODES_MAP.put("CD", "Democratic Republic of the Congo");
        COUNTRY_CODES_MAP.put("CR", "Costa Rica");
        COUNTRY_CODES_MAP.put("CI", "Côte d'Ivoire");
        COUNTRY_CODES_MAP.put("HR", "Croatia");
        COUNTRY_CODES_MAP.put("CU", "Cuba");
        COUNTRY_CODES_MAP.put("CY", "Cyprus");
        COUNTRY_CODES_MAP.put("CZ", "Czech Republic");
        COUNTRY_CODES_MAP.put("DK", "Denmark");
        COUNTRY_CODES_MAP.put("DJ", "Djibouti");
        COUNTRY_CODES_MAP.put("DM", "Dominica");
        COUNTRY_CODES_MAP.put("DO", "Dominican Republic");
        COUNTRY_CODES_MAP.put("EC", "Ecuador");
        COUNTRY_CODES_MAP.put("EG", "Egypt");
        COUNTRY_CODES_MAP.put("SV", "El Salvador");
        COUNTRY_CODES_MAP.put("GQ", "Equatorial Guinea");
        COUNTRY_CODES_MAP.put("ER", "Eritrea");
        COUNTRY_CODES_MAP.put("EE", "Estonia");
        COUNTRY_CODES_MAP.put("SZ", "Eswatini");
        COUNTRY_CODES_MAP.put("ET", "Ethiopia");
        COUNTRY_CODES_MAP.put("FJ", "Fiji");
        COUNTRY_CODES_MAP.put("FI", "Finland");
        COUNTRY_CODES_MAP.put("FR", "France");
        COUNTRY_CODES_MAP.put("GA", "Gabon");
        COUNTRY_CODES_MAP.put("GM", "Gambia");
        COUNTRY_CODES_MAP.put("GE", "Georgia");
        COUNTRY_CODES_MAP.put("DE", "Germany");
        COUNTRY_CODES_MAP.put("GH", "Ghana");
        COUNTRY_CODES_MAP.put("GR", "Greece");
        COUNTRY_CODES_MAP.put("GD", "Grenada");
        COUNTRY_CODES_MAP.put("GT", "Guatemala");
        COUNTRY_CODES_MAP.put("GN", "Guinea");
        COUNTRY_CODES_MAP.put("GW", "Guinea-Bissau");
        COUNTRY_CODES_MAP.put("GY", "Guyana");
        COUNTRY_CODES_MAP.put("HT", "Haiti");
        COUNTRY_CODES_MAP.put("HN", "Honduras");
        COUNTRY_CODES_MAP.put("HU", "Hungary");
        COUNTRY_CODES_MAP.put("IS", "Iceland");
        COUNTRY_CODES_MAP.put("IN", "India");
        COUNTRY_CODES_MAP.put("ID", "Indonesia");
        COUNTRY_CODES_MAP.put("IR", "Iran");
        COUNTRY_CODES_MAP.put("IQ", "Iraq");
        COUNTRY_CODES_MAP.put("IE", "Ireland");
        COUNTRY_CODES_MAP.put("IL", "Israel");
        COUNTRY_CODES_MAP.put("IT", "Italy");
        COUNTRY_CODES_MAP.put("JM", "Jamaica");
        COUNTRY_CODES_MAP.put("JP", "Japan");
        COUNTRY_CODES_MAP.put("JO", "Jordan");
        COUNTRY_CODES_MAP.put("KZ", "Kazakhstan");
        COUNTRY_CODES_MAP.put("KE", "Kenya");
        COUNTRY_CODES_MAP.put("KI", "Kiribati");
        COUNTRY_CODES_MAP.put("KP", "Korea, Democratic People's Republic of");
        COUNTRY_CODES_MAP.put("KR", "Korea, Republic of");
        COUNTRY_CODES_MAP.put("KW", "Kuwait");
        COUNTRY_CODES_MAP.put("KG", "Kyrgyzstan");
        COUNTRY_CODES_MAP.put("LA", "Lao People's Democratic Republic");
        COUNTRY_CODES_MAP.put("LV", "Latvia");
        COUNTRY_CODES_MAP.put("LB", "Lebanon");
        COUNTRY_CODES_MAP.put("LS", "Lesotho");
        COUNTRY_CODES_MAP.put("LR", "Liberia");
        COUNTRY_CODES_MAP.put("LY", "Libya");
        COUNTRY_CODES_MAP.put("LI", "Liechtenstein");
        COUNTRY_CODES_MAP.put("LT", "Lithuania");
        COUNTRY_CODES_MAP.put("LU", "Luxembourg");
        COUNTRY_CODES_MAP.put("MG", "Madagascar");
        COUNTRY_CODES_MAP.put("MW", "Malawi");
        COUNTRY_CODES_MAP.put("MY", "Malaysia");
        COUNTRY_CODES_MAP.put("MV", "Maldives");
        COUNTRY_CODES_MAP.put("ML", "Mali");
        COUNTRY_CODES_MAP.put("MT", "Malta");
        COUNTRY_CODES_MAP.put("MH", "Marshall Islands");
        COUNTRY_CODES_MAP.put("MR", "Mauritania");
        COUNTRY_CODES_MAP.put("MU", "Mauritius");
        COUNTRY_CODES_MAP.put("MX", "Mexico");
        COUNTRY_CODES_MAP.put("FM", "Micronesia (Federated States of)");
        COUNTRY_CODES_MAP.put("MD", "Moldova (Republic of)");
        COUNTRY_CODES_MAP.put("MC", "Monaco");
        COUNTRY_CODES_MAP.put("MN", "Mongolia");
        COUNTRY_CODES_MAP.put("ME", "Montenegro");
        COUNTRY_CODES_MAP.put("MA", "Morocco");
        COUNTRY_CODES_MAP.put("MZ", "Mozambique");
        COUNTRY_CODES_MAP.put("MM", "Myanmar");
        COUNTRY_CODES_MAP.put("NA", "Namibia");
        COUNTRY_CODES_MAP.put("NR", "Nauru");
        COUNTRY_CODES_MAP.put("NP", "Nepal");
        COUNTRY_CODES_MAP.put("NL", "Netherlands");
        COUNTRY_CODES_MAP.put("NZ", "New Zealand");
        COUNTRY_CODES_MAP.put("NI", "Nicaragua");
        COUNTRY_CODES_MAP.put("NE", "Niger");
        COUNTRY_CODES_MAP.put("NG", "Nigeria");
        COUNTRY_CODES_MAP.put("MK", "North Macedonia");
        COUNTRY_CODES_MAP.put("NO", "Norway");
        COUNTRY_CODES_MAP.put("OM", "Oman");
        COUNTRY_CODES_MAP.put("PK", "Pakistan");
        COUNTRY_CODES_MAP.put("PW", "Palau");
        COUNTRY_CODES_MAP.put("PS", "Palestine, State of");
        COUNTRY_CODES_MAP.put("PA", "Panama");
        COUNTRY_CODES_MAP.put("PG", "Papua New Guinea");
        COUNTRY_CODES_MAP.put("PY", "Paraguay");
        COUNTRY_CODES_MAP.put("PE", "Peru");
        COUNTRY_CODES_MAP.put("PH", "Philippines");
        COUNTRY_CODES_MAP.put("PL", "Poland");
        COUNTRY_CODES_MAP.put("PT", "Portugal");
        COUNTRY_CODES_MAP.put("QA", "Qatar");
        COUNTRY_CODES_MAP.put("RO", "Romania");
        COUNTRY_CODES_MAP.put("RU", "Russian Federation");
        COUNTRY_CODES_MAP.put("RW", "Rwanda");
        COUNTRY_CODES_MAP.put("KN", "Saint Kitts and Nevis");
        COUNTRY_CODES_MAP.put("LC", "Saint Lucia");
        COUNTRY_CODES_MAP.put("VC", "Saint Vincent and the Grenadines");
        COUNTRY_CODES_MAP.put("WS", "Samoa");
        COUNTRY_CODES_MAP.put("SM", "San Marino");
        COUNTRY_CODES_MAP.put("ST", "Sao Tome and Principe");
        COUNTRY_CODES_MAP.put("SA", "Saudi Arabia");
        COUNTRY_CODES_MAP.put("SN", "Senegal");
        COUNTRY_CODES_MAP.put("RS", "Serbia");
        COUNTRY_CODES_MAP.put("SC", "Seychelles");
        COUNTRY_CODES_MAP.put("SL", "Sierra Leone");
        COUNTRY_CODES_MAP.put("SG", "Singapore");
        COUNTRY_CODES_MAP.put("SK", "Slovakia");
        COUNTRY_CODES_MAP.put("SI", "Slovenia");
        COUNTRY_CODES_MAP.put("SB", "Solomon Islands");
        COUNTRY_CODES_MAP.put("SO", "Somalia");
        COUNTRY_CODES_MAP.put("ZA", "South Africa");
        COUNTRY_CODES_MAP.put("SS", "South Sudan");
        COUNTRY_CODES_MAP.put("ES", "Spain");
        COUNTRY_CODES_MAP.put("LK", "Sri Lanka");
        COUNTRY_CODES_MAP.put("SD", "Sudan");
        COUNTRY_CODES_MAP.put("SR", "Suriname");
        COUNTRY_CODES_MAP.put("SE", "Sweden");
        COUNTRY_CODES_MAP.put("CH", "Switzerland");
        COUNTRY_CODES_MAP.put("SY", "Syrian Arab Republic");
        COUNTRY_CODES_MAP.put("TJ", "Tajikistan");
        COUNTRY_CODES_MAP.put("TZ", "Tanzania");
        COUNTRY_CODES_MAP.put("TH", "Thailand");
        COUNTRY_CODES_MAP.put("TL", "Timor-Leste");
        COUNTRY_CODES_MAP.put("TG", "Togo");
        COUNTRY_CODES_MAP.put("TO", "Tonga");
        COUNTRY_CODES_MAP.put("TT", "Trinidad and Tobago");
        COUNTRY_CODES_MAP.put("TN", "Tunisia");
        COUNTRY_CODES_MAP.put("TR", "Turkey");
        COUNTRY_CODES_MAP.put("TM", "Turkmenistan");
        COUNTRY_CODES_MAP.put("TV", "Tuvalu");
        COUNTRY_CODES_MAP.put("UG", "Uganda");
        COUNTRY_CODES_MAP.put("UA", "Ukraine");
        COUNTRY_CODES_MAP.put("AE", "UAE");
        COUNTRY_CODES_MAP.put("GB", "United Kingdom");
        COUNTRY_CODES_MAP.put("US", "USA");
        COUNTRY_CODES_MAP.put("UY", "Uruguay");
        COUNTRY_CODES_MAP.put("UZ", "Uzbekistan");
        COUNTRY_CODES_MAP.put("VU", "Vanuatu");
        COUNTRY_CODES_MAP.put("VE", "Venezuela");
        COUNTRY_CODES_MAP.put("VN", "Vietnam");
        COUNTRY_CODES_MAP.put("YE", "Yemen");
        COUNTRY_CODES_MAP.put("ZM", "Zambia");
        COUNTRY_CODES_MAP.put("ZW", "Zimbabwe");
    }


    public static void main(String[] args) throws IOException {
        System.out.println(checkIP("202.191.57.199"));
    }
}