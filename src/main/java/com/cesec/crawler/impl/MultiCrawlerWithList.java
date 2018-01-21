package com.cesec.crawler.impl;



import com.cesec.crawler.Crawler;
import com.cesec.crawler.model.Song;
import com.cesec.crawler.model.WebPage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiCrawlerWithList implements Crawler {

    public List<WebPage> crawlerList;
    public List<Song> songs = new ArrayList<>();
    public static final Integer MAX_THREADS = 20;

    @Override
    public void initCrawlerList() {
        crawlerList = new ArrayList<>();
        crawlerList.add(new WebPage("http://music.163.com/discover/playlist", WebPage.PageType.playlists));
    }

    @Override
    public synchronized WebPage getUnCrawlPage() {
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

    @Override
    public Song saveSong(Song song) {
        songs.add(song);
        return song;
    }

    @Override
    public void doRun() {
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 0; i < MAX_THREADS; i++) {
            executorService.execute(new MultiCrawlerThread(this));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<Song> getSongs() {
        return songs;
    }
    
    public static <T> void main(String[] args) throws Exception {
        Date startTime = new Date();
        Crawler crawler = new MultiCrawlerWithList();
        crawler.run();
        for(Song song : crawler.getSongs()) {
            System.out.println(song);
        }
        System.out.println("花费时间：" + (new Date().getTime() - startTime.getTime()));
    }

}
