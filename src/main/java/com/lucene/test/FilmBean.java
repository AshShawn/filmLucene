package com.lucene.test;/**
 * Create by sq598 on 2019/5/16
 */

/**
 * @program: filmdemo
 * @description:
 * @author: 沈琪
 * @create: 2019-05-16 21:13
 **/

public class FilmBean {
    private String name;  //电影名
    private String score; //评分
    private String scoreNums; //评价人数
    private String url;   //链接
    private String time;  //上映时间
    private String actors; //演员


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreNums() {
        return scoreNums;
    }

    public void setScoreNums(String scoreNums) {
        this.scoreNums = scoreNums;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }
}