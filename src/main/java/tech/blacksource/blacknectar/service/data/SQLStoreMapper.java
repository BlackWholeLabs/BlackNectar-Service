/*
 * Copyright 2017 BlackSource, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.blacksource.blacknectar.service.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.inject.Inject;

import com.google.inject.ImplementedBy;
import org.springframework.jdbc.core.RowMapper;
import tech.blacksource.blacknectar.service.stores.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static tech.sirwellington.alchemy.arguments.Arguments.*;
import static tech.sirwellington.alchemy.arguments.assertions.Assertions.notNull;

/**
 * This interface is a {@link FunctionalInterface} responsible for extracting a {@link Store} from a JDBC
 * {@linkplain ResultSet Row}.
 *
 * @author SirWellington
 */
@FunctionalInterface
@ImplementedBy(SQLStoreMapper.Impl.class)
interface SQLStoreMapper extends RowMapper<Store>
{

    /**
     * Takes a JDBC {@link ResultSet} and converts it into a {@link Store}.
     *
     * @param results The SQLTools Row to read.
     * @param rowNum
     * @return A {@link Store} representation of the data, or null if it could not be extracted properly.
     * @throws SQLException
     */
    @Override
    Store mapRow(ResultSet results, int rowNum) throws SQLException;

    SQLStoreMapper INSTANCE = new Impl(new SQLTools.Impl());

    class Impl implements SQLStoreMapper
    {

        private final SQLTools sqlTools;

        @Inject
        Impl(SQLTools sqlTools)
        {
            checkThat(sqlTools).is(notNull());

            this.sqlTools = sqlTools;
        }

        @Override
        public Store mapRow(ResultSet results, int rowNum) throws SQLException
        {
            checkThat(results).is(notNull());

            //Pull data from the ResultSet
            String name = results.getString(SQLColumns.STORE_NAME);
            Double latitude = results.getDouble(SQLColumns.LATITUDE);
            if (results.wasNull())
            {
                latitude = null;
            }

            Double longitude = results.getDouble(SQLColumns.LONGITUDE);
            if (results.wasNull())
            {
                longitude = null;
            }

            UUID storeId = results.getObject(SQLColumns.STORE_ID, UUID.class);
            String address = results.getString(SQLColumns.ADDRESS_LINE_ONE);
            String addressTwo = results.getString(SQLColumns.ADDRESS_LINE_TWO);
            String city = results.getString(SQLColumns.CITY);
            String state = results.getString(SQLColumns.STATE);
            String county = results.getString(SQLColumns.COUNTY);
            String zipCode = results.getString(SQLColumns.ZIP_CODE);
            String localZip = results.getString(SQLColumns.LOCAL_ZIP_CODE);

            //Use the data to start creating a Store object, piece by piece
            Address.Builder addressBuilder = Address.Builder.newBuilder()
                                                            .withAddressLineOne(address)
                                                            .withCity(city)
                                                            .withState(state)
                                                            .withZipCode(zipCode);

            if (!isNullOrEmpty(county))
            {
                addressBuilder.withCounty(county);
            }

            if (!isNullOrEmpty(addressTwo))
            {
                addressBuilder.withAddressLineTwo(addressTwo);
            }

            if (!isNullOrEmpty(localZip))
            {
                addressBuilder.withLocalZipCode(localZip);
            }

            Store.Builder storeBuilder = Store.Builder.newInstance()
                                                      .withStoreID(storeId)
                                                      .withAddress(addressBuilder.build())
                                                      .withName(name);

            if (latitude != null && longitude != null)
            {
                Location location = Location.with(latitude, longitude);
                storeBuilder.withLocation(location);
            }

            if (sqlTools.hasColumn(results, SQLColumns.Images.URL))
            {
                String url = results.getString(SQLColumns.Images.URL);

                if (!isNullOrEmpty(url))
                {
                    storeBuilder.withMainImageURL(url);
                }
            }

            if (sqlTools.hasColumn(results, SQLColumns.STORE_CODE))
            {
                String storeCode = results.getString(SQLColumns.STORE_CODE);

                if (!isNullOrEmpty(storeCode))
                {
                    storeBuilder = storeBuilder.withStoreCode(storeCode);
                }
            }

            if (sqlTools.hasColumn(results, SQLColumns.IS_FARMERS_MARKET))
            {
                boolean isFarmersMarket = results.getBoolean(SQLColumns.IS_FARMERS_MARKET);
                storeBuilder.isFarmersMarket(isFarmersMarket);
            }

            return storeBuilder.build();
        }

    }

}
