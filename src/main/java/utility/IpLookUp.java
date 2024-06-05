package utility;

import com.maxmind.db.MaxMindDbConstructor;
import com.maxmind.db.MaxMindDbParameter;
import com.maxmind.db.Reader;
import com.maxmind.db.DatabaseRecord;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class IpLookUp {
    public static String IpCheck (String host) throws IOException {
        File database = new File("GeoLite2-Country.mmdb");
        try (Reader reader = new Reader(database)) {
            InetAddress address = InetAddress.getByName(host);

            // get() returns just the data for the associated record
            LookupResult result = reader.get(address, LookupResult.class);

            //System.out.println(result.getCountry().getIsoCode());

            // getRecord() returns a DatabaseRecord class that contains both
            // the data for the record and associated metadata.
            DatabaseRecord<LookupResult> record
                    = reader.getRecord(address, LookupResult.class);

            //System.out.println(record.getData().getCountry().getIsoCode());
            //System.out.println(record.getNetwork());

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
            return (this.country != null) ? this.country : null;
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
        System.out.println(IpCheck("127.0.0.1"));
    }
}