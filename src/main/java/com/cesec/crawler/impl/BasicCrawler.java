package com.cesec.crawler.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cesec.crawler.Crawler;
import com.cesec.crawler.HtmlParser;
import com.cesec.crawler.model.Song;
import com.cesec.crawler.model.WebPage;

public class BasicCrawler implements Crawler {
    
    private final HtmlParser htmlParser = new HtmlParser();
    /**
     * 爬虫队列
     * */
    public List<WebPage> crawlerList;

    /**
     * 歌曲列表
     * */
    public List<Song> songs;
    
    @Override
    public void initCrawlerList() {
        crawlerList = new ArrayList<>();
        crawlerList.add(new WebPage("http://music.163.com/discover/playlist", WebPage.PageType.playlists));
        songs = new ArrayList<>();
    }

    @Override
    public WebPage getUnCrawlPage(){
        WebPage webPage = new WebPage();

        if(crawlerList.size() != 0){
            webPage = crawlerList.get(0);
        }else {
            return null;
        }
        webPage.setStatus(WebPage.Status.crawled);
        crawlerList.remove(0);
        return webPage;
    }

    @Override
    public List<WebPage> addToCrawlList(List<WebPage> webPages) {
        crawlerList.addAll(webPages);
        return crawlerList;
    }

    private int count=0;
    @Override
    public Song saveSong(Song song) {
        System.out.println(count+":"+song);
        count++;
        songs.add(song);
        return song;
    }

    @Override
    public List<Song> getSongs() {
        return songs;
    }

    @Override
    public void doRun(){
        HtmlParser htmlParser = new HtmlParser();
        WebPage webPage;
            while ((webPage = getUnCrawlPage()) != null) {
                if (webPage.getType() == WebPage.PageType.playlists) {
                    addToCrawlList(htmlParser.parsePlaylists(webPage.getUrl()));
                }
                if (webPage.getType() == WebPage.PageType.playlist) {
                    addToCrawlList(htmlParser.parsePlaylist(webPage.getUrl()));
                }
                if (webPage.getType() == WebPage.PageType.song) {
                    Song song = new Song(webPage.getUrl(), webPage.getTitle(), htmlParser.parseSong(webPage.getUrl()));
                    saveSong(song);
                }
            }
    }

    @Override
    public void run(){
        initCrawlerList();
        doRun();
    }
    
    public static <T> void main(String[] args) throws Exception {
        Date startTime = new Date();
        Crawler crawler = new BasicCrawler();
        crawler.run();
        System.out.println("花费时间：" + (new Date().getTime() - startTime.getTime()));
    }
    
}
