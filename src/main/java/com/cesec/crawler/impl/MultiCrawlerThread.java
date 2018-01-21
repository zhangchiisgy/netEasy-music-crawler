package com.cesec.crawler.impl;

import com.cesec.crawler.Crawler;
import com.cesec.crawler.HtmlParser;
import com.cesec.crawler.model.Song;
import com.cesec.crawler.model.WebPage;

import java.util.ArrayList;
import java.util.List;

public class MultiCrawlerThread implements Runnable {
    
    private final Crawler multiCrawler;

    private final HtmlParser htmlParser = new HtmlParser();
    public List<WebPage> crawlerList = new ArrayList<>();
    public List<Song> songs = new ArrayList<>();

    public MultiCrawlerThread(Crawler multiCrawler) {
        super();
        this.multiCrawler = multiCrawler;
    }

    public List<WebPage> addToCrawlList(List<WebPage> webPages) {
        crawlerList.addAll(webPages);
        return webPages;
    }

    public Song saveSong(Song song) {
        this.songs.add(song);
        return song;
    }
    
    @Override
    public void run() {
        WebPage webPage;
        int getUnCrawlPageTimes = 0;
        while (true) {
            webPage = multiCrawler.getUnCrawlPage();
            if(webPage == null) {
                if(getUnCrawlPageTimes > 10) {
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    getUnCrawlPageTimes++;
                    continue;
                }
            }
            getUnCrawlPageTimes = 0;
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

}
