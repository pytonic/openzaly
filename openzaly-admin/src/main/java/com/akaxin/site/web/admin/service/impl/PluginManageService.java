/**
 * Copyright 2018-2028 Akaxin Group
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akaxin.site.web.admin.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akaxin.site.business.dao.SitePluginDao;
import com.akaxin.site.storage.bean.PluginBean;
import com.akaxin.site.web.admin.service.IPluginService;

/**
 * 扩展管理Service
 *
 * @author Sam{@link an.guoyue254@gmail.com}
 * @since 2018-04-17 11:57:26
 */
@Service("pluginManageService")
public class PluginManageService implements IPluginService {

    @Override
    public boolean addNewPlugin(PluginBean bean) {
        return SitePluginDao.getInstance().addPlugin(bean);
    }

    @Override
    public boolean deletePlugin(int pluginId) {
        return SitePluginDao.getInstance().deletePlugin(pluginId);
    }

    @Override
    public boolean updatePlugin(PluginBean bean) {
        return SitePluginDao.getInstance().updatePlugin(bean);
    }

    @Override
    public PluginBean getPlugin(int pluginId) {
        return SitePluginDao.getInstance().getPluginProfile(pluginId);
    }

    @Override
    public List<PluginBean> getPluginList(int pageNum, int pageSize) {
        return SitePluginDao.getInstance().getAllPluginList(pageNum, pageSize);
    }

    @Override
    public String reSetAuthKey(int pluginId) {
        return SitePluginDao.getInstance().reSetAuthKey(pluginId);
    }


}
