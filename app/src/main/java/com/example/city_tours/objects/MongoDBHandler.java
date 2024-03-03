package com.example.city_tours.objects;

public class MongoDBHandler {
    /*private MongoClient mongoClient;
    private MongoDatabase database;
    private static final String DB_LOGIN = BuildConfig.MONGO_DB_LOGIN;
    private static final String DB_PASSWORD = BuildConfig.MONGO_DB_PASSWORD;

    public MongoDBHandler() {
        String connectionString = String.format("mongodb+srv://%s:%s@treasurehunter.m0w0tjc.mongodb.net/?retryWrites=true&w=majority", DB_LOGIN, DB_PASSWORD);
        mongoClient = MongoClients.create(new ConnectionString(connectionString));
        database = mongoClient.getDatabase("android_tresurehunter_database");

    }

    public List<LocationMarker> getAllMarkers(){
        MongoCollection collection = database.getCollection(DATABASE_COLLECTIONS.MARKERS.getCollectionName());
        List<LocationMarker> markers = new ArrayList<>();
        for (Object document : collection.find()) {
            markers.add(new LocationMarker(
                    ((Document) document).getInteger("id"),
                    ((Document) document).getDouble("lat"),
                    ((Document) document).getDouble("lng"),
                    ((Document) document).getString("title"),
                    ((Document) document).getInteger("color"),
                    "",
                    ((Document) document).getString("description")
            ));
        }
        return markers;
    }*/
}
