package com.teamb.admin.services;

import com.teamb.admin.api.response.ProjectStatusUpdateResponse;
import com.teamb.admin.exception.EntityNotFound;
import com.teamb.admin.repositories.CharityProjectRepository;
import com.teamb.common.models.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharityProjectService {
    private final CharityProjectRepository repository;
    private final ModelMapper modelMapper;

    public ProjectStatusUpdateResponse approveCharityProject(String projectId) {
        var project = repository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        //Business validation

        //end

        project.setStatus(ProjectStatus.ACTIVE);
        project = repository.save(project);
        return modelMapper.map(project, ProjectStatusUpdateResponse.class);
    }
}
