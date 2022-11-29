package com.emory.healthAPP;

class ConvPDS {

    private float device_density;
    private float device_scaled_density;
    private float device_app_height_dp;
    private float device_app_width_dp;

    protected ConvPDS(float density, float scaledDensity, float height_dp, float width_dp) {
        this.device_density = density;
        this.device_scaled_density = scaledDensity;
        this.device_app_height_dp = height_dp;
        this.device_app_width_dp = width_dp;
    }

    protected ConvPDS(float density, float scaledDensity, int height_px, int width_px) {
        this.device_density = density;
        this.device_scaled_density = scaledDensity;
        this.device_app_height_dp = px2dp_if(height_px);
        this.device_app_width_dp = px2dp_if(width_px);
    }

    protected int i_dp2px(Object dp) {
        if (dp instanceof Integer) {
            return dp2px_ii((Integer) dp);
        }
        return dp2px_fi((Float) dp);
    }

    protected float f_dp2px(Object dp) {
        if (dp instanceof Integer) {
            return dp2px_if((Integer) dp);
        }
        return dp2px_ff((Float) dp);
    }

    protected int i_px2dp(Object px) {
        if (px instanceof Integer) {
            return px2dp_ii((Integer) px);
        }
        return px2dp_fi((Float) px);
    }

    protected float f_px2dp(Object px) {
        if (px instanceof Integer) {
            return px2dp_if((Integer) px);
        }
        return px2dp_ff((Float) px);
    }

    protected int i_sp2px(Object sp) {
        if (sp instanceof Integer) {
            return sp2px_ii((Integer) sp);
        }
        return sp2px_fi((Float) sp);
    }

    protected float f_sp2px(Object sp) {
        if (sp instanceof Integer) {
            return sp2px_if((Integer) sp);
        }
        return sp2px_ff((Float) sp);
    }

    protected int i_px2sp(Object px) {
        if (px instanceof Integer) {
            return px2sp_ii((Integer) px);
        }
        return px2sp_fi((Float) px);
    }

    protected float f_px2sp(Object px) {
        if (px instanceof Integer) {
            return px2sp_if((Integer) px);
        }
        return px2sp_ff((Float) px);
    }

    protected void setHeight(float height_dp) {
        this.device_app_height_dp = height_dp;
    }

    protected void setWidth(float width_dp) {
        this.device_app_width_dp = width_dp;
    }

    protected void setDensity(float density) {
        this.device_density = density;
    }

    protected void setScaledDensity(float density) {
        this.device_scaled_density = density;
    }

    protected float getHeight() {
        return device_app_height_dp;
    }

    protected float getWidth() {
        return device_app_width_dp;
    }

    protected float getDensity() {
        return device_density;
    }

    protected float getScaledDensity() {
        return device_scaled_density;
    }

    protected float dp2px_ff(float dp) {
        return dp * device_density;
    }

    protected float px2dp_ff(float px) {
        return px / device_density;
    }

    protected int dp2px_fi(float dp) {
        return Math.round(dp * device_density);
    }

    protected int px2dp_fi(float px) {
        return Math.round(px / device_density);
    }

    protected int dp2px_ii(int dp) {
        return Math.round(((float) dp) * device_density);
    }

    protected int px2dp_ii(int px) {
        return Math.round(((float) px) / device_density);
    }

    protected float dp2px_if(int dp) {
        return ((float) dp) * device_density;
    }

    protected float px2dp_if(int px) {
        return ((float) px) / device_density;
    }

    protected float sp2px_ff(float sp) {
        return sp * device_scaled_density;
    }

    protected float px2sp_ff(float px) {
        return px / device_scaled_density;
    }

    protected int sp2px_fi(float sp) {
        return Math.round(sp * device_scaled_density);
    }

    protected int px2sp_fi(float px) {
        return Math.round(px / device_scaled_density);
    }

    protected int sp2px_ii(int sp) {
        return Math.round(((float) sp) * device_scaled_density);
    }

    protected int px2sp_ii(int px) {
        return Math.round(((float) px) / device_scaled_density);
    }

    protected float sp2px_if(int sp) {
        return ((float) sp) * device_scaled_density;
    }

    protected float px2sp_if(int px) {
        return ((float) px) / device_scaled_density;
    }

    protected int dp2sp_fi(float dp) {return Math.round(dp2px_ff(dp) / device_scaled_density);}

    protected int sp2dp_fi(float sp) {return Math.round(sp2px_ff(sp) / device_density);}

    protected float dp2sp_if(int dp) {return (dp2px_if(dp) / device_scaled_density);}

    protected float sp2dp_if(int sp) {return (sp2px_if(sp) / device_density);}

    protected int dp2sp_ii(int dp) {return Math.round(dp2px_if(dp) / device_scaled_density);}

    protected int sp2dp_ii(int sp) {return Math.round(sp2px_if(sp) / device_density);}

    protected float dp2sp_ff(float dp) {return (dp2px_ff(dp) / device_scaled_density);}

    protected float sp2dp_ff(float sp) {return (sp2px_ff(sp) / device_density);}
}
