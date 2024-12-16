package com.teamb.admin.services;

import com.teamb.admin.api.response.ProjectStatusUpdateResponse;
import com.teamb.common.exception.EntityNotFound;
import com.teamb.admin.repositories.CharityProjectRepository;
import com.teamb.charity.models.CharityProject;
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
        CharityProject project = repository.findById(projectId).orElseThrow(() -> new EntityNotFound("projectId", projectId));

        //Business validation
        switch (project.getStatus()) {
            case ACTIVE -> throw new IllegalArgumentException("This charity project is already active");
            case HALTED -> throw new IllegalStateException("Charity project is halted");
            case COMPLETED -> throw new IllegalArgumentException("Charity project is completed");
        }
        //end

        project.setStatus(ProjectStatus.ACTIVE);
        project = repository.save(project);
        return modelMapper.map(project, ProjectStatusUpdateResponse.class);
    }
}
