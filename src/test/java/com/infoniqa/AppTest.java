package com.infoniqa;

import com.infoniqa.persistence.Department;
import com.infoniqa.persistence.Department_;
import com.infoniqa.persistence.Employee;
import com.infoniqa.persistence.Employee_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AppTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    // Given initial Data
    private final Department department = new Department("OrangeRoom");
    private final Department department1 = new Department("GreenRoom");
    private final Department department2 = new Department("RedRoom");
    private final Employee employee = new Employee("mrBlack", department);
    private final Employee employee1 = new Employee("mrPink", department);
    private final Employee employee2 = new Employee("mrBrown", department1);
    private final Employee employee3 = new Employee("mrPurple", department1);
    private final Employee employee4 = new Employee("mrBlue", department2);
    private final Employee employee5 = new Employee("mrWhite", department2);

    @Test
    void shouldUseJpaWithCriteriaQuery() {
        try {
            emf = Persistence.createEntityManagerFactory("default");
            em = emf.createEntityManager();

            fillInDB();

            //Find departments which have employees whose names start with 'mrP*'
            List<Department> selectedDepartments = getDepartmentsIfHaveAnyEmployeeByName("%mrP%");
            Assertions.assertThat(selectedDepartments).hasSize(2)
                    .containsExactlyInAnyOrder(department, department1);

            //Find departments which have employees whose names start with 'mrB*'
            selectedDepartments = getDepartmentsIfHaveAnyEmployeeByName("%mrB%");
            Assertions.assertThat(selectedDepartments).hasSize(3)
                    .containsExactlyInAnyOrder(department, department1, department2);

            //Find departments which have employees whose names start with 'mrW*'
            selectedDepartments = getDepartmentsIfHaveAnyEmployeeByName("%mrW%");
            Assertions.assertThat(selectedDepartments).hasSize(1)
                    .containsExactly(department2);

            //Find departments which have employees whose names start with 'mrX*'
            selectedDepartments = getDepartmentsIfHaveAnyEmployeeByName("%mrX%");
            Assertions.assertThat(selectedDepartments).isEmpty();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (emf != null) {
                emf.close();
            }
            if (em != null) {
                em.close();
            }
        }
    }

    private void fillInDB() {
        //Insert
        em.getTransaction().begin();
        em.persist(department);
        em.persist(department1);
        em.persist(department2);
        em.persist(employee);
        em.persist(employee1);
        em.persist(employee2);
        em.persist(employee3);
        em.persist(employee4);
        em.persist(employee5);
        em.getTransaction().commit();
    }

    private List<Department> getDepartmentsIfHaveAnyEmployeeByName(String inputParam) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> cq = cb.createQuery(Department.class);
        Root<Department> root = cq.from(Department.class);
        SetJoin<Department, Employee> books = root.join(Department_.employees);

        ParameterExpression<String> paramTitle = cb.parameter(String.class);
        cq.where(cb.like(books.get(Employee_.name), paramTitle));

        TypedQuery<Department> q = em.createQuery(cq);
        q.setParameter(paramTitle, inputParam);
        return q.getResultList();
    }
}