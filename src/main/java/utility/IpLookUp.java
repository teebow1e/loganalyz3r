package utility;

import com.maxmind.db.MaxMindDbConstructor;
import com.maxmind.db.MaxMindDbParameter;
import com.maxmind.db.Reader;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class IpLookUp {
    public static String IpCheck (String host) throws IOException {
        File database = new File("GeoLite2-Country.mmdb");
        try (Reader reader = new Reader(database)) {
            InetAddress address = InetAddress.getByName(host);
            LookupResult result = reader.get(address, LookupResult.class);

            if (result != null && result.getCountry() != null) {
                return result.getCountry().getIsoCode();
            } else {
                return "None";
            }
        }
    }

    public static class LookupResult {
        private final Country country;

        @MaxMindDbConstructor
        public LookupResult (
                @MaxMindDbParameter(name="country") Country country
        ) {
            this.country = country;
        }

        public Country getCountry() {
            return this.country;
        }
    }

    public static class Country {
        private final String isoCode;
        @MaxMindDbConstructor
        public Country (
                @MaxMindDbParameter(name="iso_code") String isoCode
        ) {
            this.isoCode = isoCode;
        }

        public String getIsoCode() {
            return this.isoCode;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(IpCheck("202.191.57.199"));
    }
}