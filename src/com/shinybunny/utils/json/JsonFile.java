package com.shinybunny.utils.json;

import com.shinybunny.utils.fs.File;
import com.shinybunny.utils.fs.Folder;

public class JsonFile extends Json {

    private final boolean autoSave;
    private File file;

    private JsonFile(File file, JsonHelper helper, boolean autoSave) {
        super(helper);
        this.file = file;
        this.autoSave = autoSave;
    }

    public JsonFile(String path) {
        this(path,false);
    }

    public JsonFile(String path, boolean autoSave) {
        this.file = File.of(path);
        this.addAll(helper.load(file));
        this.autoSave = autoSave;
    }

    @Override
    protected void onEntryChanged(String path, Json value) {
        if (autoSave) {
            save(true);
        }
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
        return of(File.of(ioFile));
    }

    public void save(boolean prettyPrint) {
        file.setContent(prettyPrint ? PrettyPrinter.print(this,4) : this.toString());
    }

    public void update() {
        this.replaceAllWith(helper.load(file));
    }

    public static class Loader {

        private final File file;
        private JsonHelper helper;
        private boolean autoSave;

        public Loader(File file) {
            this.file = file;
            this.helper = JsonHelper.DEFAULT_HELPER;
        }

        public Loader autoSave(boolean enable) {
            this.autoSave = enable;
            return this;
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
            JsonFile f = new JsonFile(file,helper,autoSave);
            f.addAll(json);
            return f;
        }

    }

}
