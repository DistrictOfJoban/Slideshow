package org.teacon.slides.renderer;

import com.google.common.base.Objects;

public class SlideStateProperties {
    private final String location;
    private final boolean enableLod;

    public SlideStateProperties(String location, boolean enableLod) {
        this.location   = location;
        this.enableLod  = enableLod;
    }

    public String  getLocation() {
        return location;
    }

    public boolean getEnableLod() {
        return enableLod;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SlideStateProperties that) {
            return enableLod == that.enableLod && Objects.equal(location, that.location);
        }

        return false;
    }    

    @Override public int hashCode() {
        return Objects.hashCode(enableLod, location);
    }
}