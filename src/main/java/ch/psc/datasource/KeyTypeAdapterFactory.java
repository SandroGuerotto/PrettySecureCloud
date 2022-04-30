package ch.psc.datasource;

import java.io.IOException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class KeyTypeAdapterFactory implements TypeAdapterFactory {
  
  private static final String NAME_KEY = "key";
  private static final String NAME_ALGORITHM = "algorithm";

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if(!Key.class.isAssignableFrom(type.getRawType())) {
      return null;
    }
    
    return new TypeAdapter<T>() {

      @Override
      public void write(JsonWriter out, T value) throws IOException {
        if(null == value) {
          out.nullValue();
        }
        else {
          Key key = (Key) value;
          out.beginObject();
          out.name(NAME_KEY);
          out.value(new String(key.getEncoded()));
          out.name(NAME_ALGORITHM);
          out.value(key.getAlgorithm());
          out.endObject();
        }
      }

      @Override
      public T read(JsonReader in) throws IOException {
        if(JsonToken.NULL == in.peek()) {
          in.nextNull();
          return null;
        }
        else {
          
          byte[] key = null;
          String algorithm = null;
          
          in.beginObject();
          while(in.hasNext()) {
            JsonToken token = in.peek();
            String fieldName = null;
          
            if(token.equals(JsonToken.NAME)) {
              fieldName = in.nextName();
              
              if(NAME_KEY.equals(fieldName)) {
                key = in.nextString().getBytes();
              }
              else if(NAME_ALGORITHM.equals(fieldName)) {
                algorithm = in.nextString();
              }
            }
          }
          in.endObject();
          
          if(null == key) {
            throw new JsonSyntaxException("key needs to be set!");
          }
          if(null == algorithm) {
            throw new JsonSyntaxException("algorithm needs to be set!");
          }
          
          @SuppressWarnings("unchecked")
          T secretKey = (T) new SecretKeySpec(key, algorithm);
          return secretKey;
        }
      }
      
    };
  }

}
