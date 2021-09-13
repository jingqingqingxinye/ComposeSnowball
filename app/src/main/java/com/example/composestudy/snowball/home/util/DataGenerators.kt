/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composestudy.snowball.home.util

import com.example.composestudy.snowball.home.data.PostData

fun generateRandomPostItem(tabName: String? = ""): PostData {
    val name = listOf(
        "紫金矿业(SH601899)",
        "顺丰股份(SH601899)",
        "爱美客(SH601899)",
        "宁德时代(SH601899)",
        "比亚迪(SH601899)",
        "长安汽车(SH601899)",
        "恒瑞医药(SH601899)",
        "中国平安(SH601899)",
        "贵州茅台(SH601899)"
    ).random()
    val content = listOf(
        "嘉宾：@余则成同志，访谈时间：2021-07-01 16：00-17：00，向嘉宾提问：",
        "同花顺（300033）问财数据统计显示，隆基股份（601012）06月29日融资融券余额为101.97亿元。其中，融资余额为98.8亿元",
        "06月29日恒瑞医药融资净买入2.00亿元，两市排名第7",
        "原标题：顺丰控股：子公司同城实业已向港交所递交首次公开发行境外上市外资股（H股）并在港交所主板上市的",
        "顺丰控股：子公司同城实业已向港交所递交首次公开发行境外上市外资股（H股）并在港交所主板上市的申请",
        "各企业竞相布局动力电池回收业务的原因何在？",
        "近年来，大量车企、动力电池企业竞相布局动力电池回收业务，如早在2013年，宁德时代通过对邦普循环完成收购布局电池回收业务;2018年，上汽集团就与宁德时代达成共同推进新能",
        "智通财经APP获悉，据港交所6月30日披露，顺丰控股拆分出的子公司杭州顺丰同城实业股份有限公司向港交所主板提交上市申请，美银证券及中金公司为联席保荐人。 根据艾瑞报告的资料",
        "同花顺（300033）金融研究中心6月30日讯，有投资者向龙蟒佰利（002601）提问， 问董秘 现在印度的订单大约占比有多少？疫情原因会对订单有多大影响，"
    ).random()
    return PostData(stockId = "601899",
        userName = name,
        postContent = content,
        publishTime = "23分钟前",
        postSource = "xueqiu.com",
        postTitle = "雪球访谈：黄金赛道CRO，你上车了吗")
}
