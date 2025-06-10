package com.menumitra.apiRequest;

public class BannerCreateRequest {
    private Long userId;
    private String name;
    private String bannerImage;
    private Integer showCds;
    private Integer showUserApp;
    private Integer postOnInstagram;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Integer getShowCds() {
        return showCds;
    }

    public void setShowCds(Integer showCds) {
        this.showCds = showCds;
    }

    public Integer getShowUserApp() {
        return showUserApp;
    }

    public void setShowUserApp(Integer showUserApp) {
        this.showUserApp = showUserApp;
    }

    public Integer getPostOnInstagram() {
        return postOnInstagram;
    }

    public void setPostOnInstagram(Integer postOnInstagram) {
        this.postOnInstagram = postOnInstagram;
    }
    
}
