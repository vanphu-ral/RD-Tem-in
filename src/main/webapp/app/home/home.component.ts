import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { Component, OnInit } from '@angular/core';

import { LoginService } from 'app/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  standalone: false,
})
export class HomeComponent implements OnInit {
  dropdownList: { item_id: number; item_text: string }[] = [];
  selectedList: { item_id: number; item_text: string }[] = [];
  dropdownSetting?: IDropdownSettings;
  account: Account | null = null;

  constructor(
    private accountService: AccountService,
    private loginService: LoginService,
  ) {}

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => (this.account = account));
  }

  login(): void {
    this.loginService.login();
  }
}
