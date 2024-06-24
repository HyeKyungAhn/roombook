package site.roombook.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import site.roombook.domain.FileDto;

import java.util.List;
import java.util.Map;

@Repository
public class FileDaoImpl implements FileDao{

    @Autowired
    SqlSession session;

    private static final String namespace = "site.roombook.dao.fileMapper.";

    @Override
    public int insertFiles(List<FileDto> list) {
        return session.insert(namespace+"insertFiles", list);
    }

    @Override
    public List<FileDto> selectAllFiles() {
        return session.selectList(namespace+"selectAll");
    }

    @Override
    public List<FileDto> selectFilesWithSpaceData(Map<String, Object> spaceData) {
        return session.selectList(namespace+"selectFilesWithSpaceData", spaceData);
    }

    @Override
    public int selectALlFilesCnt() {
        return session.selectOne(namespace+"selectAllCnt");
    }

    @Override
    public FileDto selectOneFileWithSpaceData(Map<String, Object> spaceData) {
        return session.selectOne(namespace+"selectOneFileWithSpaceData", spaceData);
    }

    @Override
    public int checkExceedingMaxFileCnt(FileDto filedto) {
        return session.selectOne(namespace+"checkExceedingMaxFileCnt", filedto);
    }

    @Override
    public int deleteAll() {
        return session.delete(namespace+"deleteAll");
    }

    @Override
    public int deleteWithNames(List<String> fileNames) {
        return session.delete(namespace+"deleteWithNames", fileNames);
    }


}
