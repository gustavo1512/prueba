import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { PersonaFormService, PersonaFormGroup } from './persona-form.service';
import { IPersona } from '../persona.model';
import { PersonaService } from '../service/persona.service';

@Component({
  standalone: true,
  selector: 'jhi-persona-update',
  templateUrl: './persona-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PersonaUpdateComponent implements OnInit {
  isSaving = false;
  persona: IPersona | null = null;

  editForm: PersonaFormGroup = this.personaFormService.createPersonaFormGroup();

  constructor(
    protected personaService: PersonaService,
    protected personaFormService: PersonaFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ persona }) => {
      this.persona = persona;
      if (persona) {
        this.updateForm(persona);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const persona = this.personaFormService.getPersona(this.editForm);
    if (persona.id !== null) {
      this.subscribeToSaveResponse(this.personaService.update(persona));
    } else {
      this.subscribeToSaveResponse(this.personaService.create(persona));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPersona>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(persona: IPersona): void {
    this.persona = persona;
    this.personaFormService.resetForm(this.editForm, persona);
  }
}
