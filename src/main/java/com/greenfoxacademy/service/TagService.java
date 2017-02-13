package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.Tag;
import com.greenfoxacademy.repository.TagRepository;
import com.greenfoxacademy.responses.TagsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by BSoptei on 2/7/2017.
 */
@Service
public class TagService {

    private TagRepository tagRepository;
    private CommonTasksService commonTasksService;

    @Autowired
    public TagService(TagRepository tagRepository,
                      CommonTasksService commonTasksService){
        this.tagRepository = tagRepository;
        this.commonTasksService = commonTasksService;
    }

    public List<Tag> findTagsByString(String tagsRaw) {
        String[] tags = tagsRaw.split(",");
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].trim();
        }
        return tagRepository.findByTagNameIn(tags);
    }

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    public Tag findTagById(Long id) {
        return tagRepository.findOne(id);
    }

    public void saveTag(Tag tag){
        tagRepository.save(tag);
    }

    public List<Tag> recommendTagsByBeginning(String rawPattern){
        String pattern = rawPattern.concat("%");
        return tagRepository.findByBeginningLike(pattern);
    }

    public Tag findByTagName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    public void emptyRepositoryBeforeTest() {
        tagRepository.deleteAll();
    }

    public ResponseEntity showTags() {
        List<Tag> allTags = findAllTags();
        TagsResponse tagsResponse = new TagsResponse();
        for (Tag tag : allTags) {
            tagsResponse.getTags().add(tag.getTagName());
        }
        return commonTasksService.showCustomResults(tagsResponse, HttpStatus.OK);
    }

}
