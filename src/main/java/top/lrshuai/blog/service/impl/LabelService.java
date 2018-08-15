package top.lrshuai.blog.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.lrshuai.blog.dao.LabelDao;
import top.lrshuai.blog.service.ILabelService;
import top.lrshuai.blog.util.ParameterMap;

@Service
public class LabelService implements ILabelService {

	@Autowired
	private LabelDao labelDao;

	@Override
	public List<ParameterMap> getArticleLabels(ParameterMap pm) throws Exception {
		return labelDao.getArticleLabels(pm);
	}

	@Override
	public int delArticleLabel(ParameterMap pm) throws Exception {
		// TODO Auto-generated method stub
		return labelDao.delArticleLabel(pm);
	}
}
