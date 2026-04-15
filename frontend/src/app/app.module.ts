import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule }  from './app-routing.module';
import { AppComponent }      from './app.component';
import { LoginComponent }    from './pages/login/login.component';
import { SignupComponent }   from './pages/signup/signup.component';
import { ManagerComponent }  from './pages/manager/manager.component';
import { EmployeeComponent } from './pages/employee/employee.component';
import { ProfileComponent }  from './pages/profile/profile.component';
import { JwtInterceptor }    from './interceptors/jwt.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SignupComponent,
    ManagerComponent,
    EmployeeComponent,
    ProfileComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
