package com.simulator.exam.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ModuleEnum {
    SPRING_FRAMEWORK(1, "Spring Framework", "Container, Dependency, and IoC", "questions-files/Module_1.yaml"),
    SPRING_AOP(2, "Spring AOP", "Aspect Oriented Programming", "questions-files/Module_2.yaml"),
    SPRING_DATA_JPA(3, "Spring Data JPA", "Data Management: JDBC, Transactions, Spring Data JPA", "Module_3.yaml"),
    SPRING_BOOT(4, "Spring Boot",
            "Spring Boot, Spring Boot Auto Configuration, Spring Boot Actuator, Spring Boot Testing", "Module_4.yaml"),
    SPRING_MVC(5, "Spring MVC", "Spring MVC and the Web Layer", "Module_5.yaml"),
    SPRING_SECURITY(6, "Spring Security", "Spring Security", "Module_6.yaml"),
    SPRING_REST(7, "Spring REST", "Spring REST", "Module_7.yaml"),
    SPRING_TESTING(8, "Spring Testing", "Spring Testing", "Module_8.yaml");

    private final int moduleNumber;
    private final String moduleName;
    private final String moduleDescription;
    private final String moduleQuestionFile;

    /**
     * Returns the Module enum entry by module number.
     *
     * @param id
     * @return the enum entry
     */
    public static ModuleEnum byModuleNumber(final int number) {
        for (final ModuleEnum module : ModuleEnum.values()) {
            if (module.getModuleNumber() == number) {
                return module;
            }
        }
        return null;
    }

    public static ModuleEnum byModuleName(final String name) {
        for (final ModuleEnum module : ModuleEnum.values()) {
            if (module.getModuleName().equals(name)) {
                return module;
            }
        }
        return null;
    }
}
