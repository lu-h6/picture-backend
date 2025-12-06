package com.lgh.picturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
	 *搜索词
	 */
    private String searchText;

    /**
     *抓取数量
     */
    private Integer count = 10;

    /**
     *图片名称前缀
     */
    private String namePrefix;

    private static final long serialVersionUID = 1L;  
}

