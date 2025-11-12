package com.example.vocabularybuilder.api;

import com.example.vocabularybuilder.data.model.Word;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class ApiClient {

    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface ApiService {
        @GET("{word}")
        Call<List<ApiWord>> getWordDefinition(@Path("word") String word);
    }

    // --- Data Models for GSON Parsing ---

    public static class ApiWord {
        @SerializedName("word")
        private String word;

        @SerializedName("meanings")
        private List<Meaning> meanings;

        public String getWord() {
            return word;
        }

        public List<Meaning> getMeanings() {
            return meanings;
        }
    }

    public static class Meaning {
        @SerializedName("partOfSpeech")
        private String partOfSpeech;

        @SerializedName("definitions")
        private List<Definition> definitions;

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public List<Definition> getDefinitions() {
            return definitions;
        }
    }

    public static class Definition {
        @SerializedName("definition")
        private String definition;

        @SerializedName("example")
        private String example;

        public String getDefinition() {
            return definition;
        }

        public String getExample() {
            return example;
        }
    }
}
