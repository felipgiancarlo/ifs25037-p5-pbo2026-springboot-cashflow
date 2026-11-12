package org.delcom.felipppspringbootcashflow;

import org.delcom.felipppspringbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.felipppspringbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.felipppspringbootcashflow.domain.repository.IUserRepository;
import org.delcom.felipppspringbootcashflow.usecase.AuthUseCase;
import org.delcom.felipppspringbootcashflow.usecase.FinanceUseCase;
import org.delcom.felipppspringbootcashflow.usecase.UserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ActiveProfiles("test")
class AppTest {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private FinanceUseCase financeUseCase;

    @Autowired
    private AuthUseCase authUseCase;

    @Autowired
    private UserUseCase userUseCase;

    @Autowired
    private FinancePresenter financePresenter;

    @Test
    void contextLoadsAndWiresCleanArchitectureBeans() {
        assertNotNull(transactionRepository);
        assertNotNull(userRepository);
        assertNotNull(financeUseCase);
        assertNotNull(authUseCase);
        assertNotNull(userUseCase);
        assertNotNull(financePresenter);
    }

    @Test
    void mainDelegatesToSpringApplicationRun() throws Exception {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(
                    eq(App.class),
                    any(String[].class)
            )).thenReturn(context);

            App.main(new String[]{"--server.port=0"});

            mocked.verify(() -> SpringApplication.run(
                    eq(App.class),
                    any(String[].class)
            ));
        }
    }
}
