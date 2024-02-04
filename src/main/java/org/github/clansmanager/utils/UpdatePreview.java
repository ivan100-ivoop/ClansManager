package org.github.clansmanager.utils;

import org.github.clansmanager.Loader;

public class UpdatePreview {
    private String message;

    private UpdateInfo data;
    private boolean error;

    public boolean isHaveError(){
        return this.error;
    }

    public UpdateInfo getData(){
        return this.data;
    }

    public String getMessage(){
        return this.message;
    }

    public class UpdateInfo{
        private String html_url;
        private String tag_name;

        public String getHtmlUrl(String defaultValue) {
            if(html_url == null && html_url.isEmpty()) {
                return defaultValue;
            }
            return html_url;
        }

        public String getTagName(String defaultValue) {
            if(tag_name == null && tag_name.isEmpty()){
                return defaultValue;
            }
            return tag_name;
        }
    }
}
