package com.cesec.crawler.impl;

import com.cesec.crawler.Crawler;
import com.cesec.crawler.HtmlParser;
import com.cesec.crawler.model.Song;
import com.cesec.crawler.model.WebPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BasicCrawler implements Crawler {
    
    private final HtmlParser htmlParser = new HtmlParser();
    public List<WebPage> crawlerList;
    public List<Song> songs = new ArrayList<>();
    
    @Override
    public void initCrawlerList() {
        crawlerList = new ArrayList<WebPage>();
//        for(int i = 0; i < 43; i++) {
//            crawlerList.add(new WebPage("http://music.163.com/discover/playlist/?order=hot&cat=%E5%85%A8%E9%83%A8&limit=35&offset="  + (i * 35), PageType.playlists));
//        }
        crawlerList.add(new WebPage("http://music.163.com/playlist?id=454016843", WebPage.PageType.playlist));
    }

    @Override
    public WebPage getUnCrawlPage() {
        if(crawlerList.isEmpty()) {
            return null;
        }
        return crawlerList.remove(0);
    }

    @Override
    public List<WebPage> addToCrawlList(List<WebPage> webPages) {
        this.crawlerList.addAll(webPages);
        return webPages;
    }

    @Override
    public Song saveSong(Song song) {
        this.songs.add(song);
        return song;
    }

    @Override
    public void doRun() {
        WebPage webPage;
        while ((webPage = getUnCrawlPage()) != null) {
            if(WebPage.PageType.playlists.equals(webPage.getType())) {
                addToCrawlList(htmlParser.parsePlaylists(webPage.getUrl()));
            } else if(WebPage.PageType.playlist.equals(webPage.getType())) {
                addToCrawlList(htmlParser.parsePlaylist(webPage.getUrl()));
            } else {
                Song song = new Song(webPage.getUrl(), webPage.getTitle(), htmlParser.parseSong(webPage.getUrl()));
                saveSong(song);
            }
        }
    }

    @Override
    public List<Song> getSongs() {
        return songs;
    }
    
    public static <T> void main(String[] args) throws Exception {
        Date startTime = new Date();
        Crawler crawler = new BasicCrawler();
        crawler.run();
        for(Song song : crawler.getSongs()) {
            System.out.println(song);
        }
        System.out.println("花费时间：" + (new Date().getTime() - startTime.getTime()));
    }
    
}
