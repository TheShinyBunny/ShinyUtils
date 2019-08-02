package com.shinybunny.utils.json;

import com.shinybunny.utils.fs.File;
import com.shinybunny.utils.fs.Folder;

public class JsonFile extends Json {

    private File file;

    private JsonFile(File file, JsonHelper helper) {
        super(helper);
        this.file = file;
    }

    public static Loader of(String path) {
        return of(File.of(path));
    }

    public static Loader of(Folder parent, String name) {
        return of(File.of(parent,name));
    }

    public static Loader of(File file) {
        return new Loader(file);
    }

    public static Loader of(java.io.File ioFile) {
        return of(File.from(ioFile));
    }

    public static class Loader {

        private final File file;
        private JsonHelper helper;

        public Loader(File file) {
            this.file = file;
            this.helper = JsonHelper.DEFAULT_HELPER;
        }

        public Loader withHelper(JsonHelper helper) {
            this.helper = helper;
            return this;
        }

        public Loader withAdapter(JsonAdapter<?> adapter) {
            helper.withAdapter(adapter);
            return this;
        }

        public <T> Loader serializer(Class<T> type, JsonSerializer<T> serializer) {
            return withAdapter(JsonAdapter.from(type,serializer));
        }

        public JsonFile load() {
            Json json = helper.load(file);
            JsonFile f = new JsonFile(file,helper);
            f.addAll(json);
            return f;
        }

    }

}
