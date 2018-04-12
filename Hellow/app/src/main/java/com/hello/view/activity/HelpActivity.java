package com.hello.view.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.hello.R;
import com.hello.databinding.ActivityHelpBinding;
import com.hello.model.data.HelpFunData;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppActivity {
    private ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_help);
        binding.setActivity(this);
        binding.setData(new ArrayList<>());

        init();
    }

    private void init() {
        List<HelpFunData> list = new ArrayList<>();
        list.add(new HelpFunData("闲聊", "想我了吗", "我肯定非常非常想你啊"));
        list.add(new HelpFunData("应用", "打开QQ", "已为你打开QQ"));
        list.add(new HelpFunData("计算", "2的10次方", "等于1024"));
        list.add(new HelpFunData("电筒", "打开电筒", "已开启"));
        list.add(new HelpFunData("导航", "导航去天府广场", "正在为您导航"));
        list.add(new HelpFunData("短信", "发短信给张三说你媳妇跑啦", "请确认短信内容"));
        list.add(new HelpFunData("电话", "打电话给张三", "好的"));
        list.add(new HelpFunData("附近", "我周围有什么好吃的", "已为你找到附近的好吃的"));
        list.add(new HelpFunData("百科", "火箭是什么", "火箭有很多种，原始的火箭是用..."));
        list.add(new HelpFunData("限行", "今天限行的尾号是多少", "为您找到北京的限行尾号：2和7"));
        list.add(new HelpFunData("生肖", "1996年出生属什么", "1996年的生肖为鼠"));
        list.add(new HelpFunData("星座", "处女座的运势", "今天你可能会突然发觉很多事情都不是..."));
        list.add(new HelpFunData("菜谱", "回锅肉怎么做", "回锅肉的做法如下："));
        list.add(new HelpFunData("相声", "我要听相声", "请欣赏姥姥年"));
        list.add(new HelpFunData("时间", "几点了", "现在是2018年04月12日 星期..."));
        list.add(new HelpFunData("节假日", "劳动节还有多久", "劳动节是2018年05月...还有19天"));
        list.add(new HelpFunData("日期", "今天的农历日期", "今天是2018年04月12日 戊戌..."));
        list.add(new HelpFunData("戏曲", "我要听川剧", "川剧是中国戏曲宝库中的一颗光彩..."));
        list.add(new HelpFunData("周公解梦", "梦见蛇代表什么", "梦见蛇表示：得官，大吉。梦脚..."));
        list.add(new HelpFunData("每日英语", "我要学英语", "为您找到以下内容：Believe that..."));
        list.add(new HelpFunData("健康知识", "头晕怎么办", "为您找到：《学中医公益课堂开课..."));
        list.add(new HelpFunData("成语接龙", "为所欲为成语接龙", "为非作歹"));
        list.add(new HelpFunData("成语查询", "马到成功的含义", " 形容工作刚开始就取得成功。"));
        list.add(new HelpFunData("笑话", "讲个笑话", "老师：今年是猪年，你像猪一样吃饭、像猪一样睡觉..."));
        list.add(new HelpFunData("音乐", "来一首音乐", "听下张学友的爱很简单吧"));
        list.add(new HelpFunData("新闻", "最近有什么科技新闻", "为您播放科技的相关新闻"));
        list.add(new HelpFunData("诗词", "白日依山尽的下一句", "黄河入海流"));
        list.add(new HelpFunData("收音机", "我想听fm102.6", "为您找到渭南人民广播电台新闻综合广播"));
        list.add(new HelpFunData("提醒", "明天早上9点提醒我买菜", "明天早上9点我会提醒您"));
        list.add(new HelpFunData("故事", "讲个故事", "请听故事白雪公主"));
        list.add(new HelpFunData("笔画", "美丽的美怎么写", "为您找到“美”的笔画：[捺,撇,横,横,竖,横,横,撇,捺]。"));
        list.add(new HelpFunData("翻译", "翻译一下你好吗", "How are you?"));
        list.add(new HelpFunData("天气", "外面冷不冷", "成都今天小雨，气温15度，无持续风向微风"));
        list.add(new HelpFunData("近义词", "安静的近义词", "为您找到近义词：[孤独,萧条,安宁,清幽,背静,镇定..."));
        list.add(new HelpFunData("反义词", "有哪些词和安静的意思相反", "为您找到反义词：[闹热,惊愕,哗闹,骚闹,颤..."));
        list.add(new HelpFunData("脑筋急转弯", "来一个脑筋急转弯", "好的，看看这一题怎么样！问题：阿弟竟..."));
        list.add(new HelpFunData("谜语", "说一个谜语", "谜题：一座军营百个兵，列好队伍等命..."));
        list.add(new HelpFunData("绕口令", "说个绕口令", "化肥会挥发，灰化肥会发黑，黑化肥会发..."));
        list.add(new HelpFunData("顺口溜", "说个顺口溜", "青年人忙休闲，中年人忙事业，老年..."));
        list.add(new HelpFunData("藏头诗", "我喜欢你藏头诗", "青年人忙休闲，中年人忙事业，老年..."));
        list.add(new HelpFunData("果蔬报价", "海南芒果的价格", "亲，已找到在海南芒果价格行情"));
        list.add(new HelpFunData("汽油价格", "北京汽油的价格", "亲，已帮你找到汽油的价格信息"));
        list.add(new HelpFunData("股票", "腾讯的股票", "亲，已帮你找到股票信息"));
        list.add(new HelpFunData("邮编", "北京的邮编", "北京的邮编是100000"));
        list.add(new HelpFunData("快递", "查询快递123456789", "2016-06-23 02:00:00 货物已到达【杭州分拣..."));

        binding.setData(list);
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
    }
}
